package com.bpjamsostek.ceph.api.controller

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.util.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URL
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
class CephController(
    @Value("\${storage.url}") private val storageUrl: String
) {

    @Autowired
    private val amazonS3: AmazonS3? = null

    @GetMapping(value = ["/**"])
    fun getObject(request: HttpServletRequest, response: HttpServletResponse){
        try {
            val fileStream: InputStream = URL(storageUrl + request.requestURI).openStream()
            IOUtils.copy(fileStream, response.outputStream)
            response.flushBuffer()
        }catch (e: IOException){
            val out = response.writer
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            out.print("File not found")
            out.flush()
        }catch (e: FileNotFoundException){
            println(e.message.toString())
        }
    }

    @GetMapping(value = ["/file/**"])
    fun getObjectOld(request: HttpServletRequest, response: HttpServletResponse){
        try {
            val splitChar = request.requestURI.split("/")
            var key = ""
            var urlKey = ""
            val contentTypeIdx = splitChar.count() - 2
            var i = 1
            splitChar.forEach {
                if (i > 3 && i !=splitChar.count()-1){
                    key += if (i!=splitChar.count()){
                        "$it/"
                    }else{
                        it
                    }
                }
                if (i > 2 && i !=splitChar.count()-1){
                    urlKey += if (i!=splitChar.count()){
                        "$it/"
                    }else{
                        it
                    }
                }
                i += 1
            }

            var mimeType = ""
            when(splitChar[contentTypeIdx]){
                "jpg" -> {
                    mimeType = MediaType.IMAGE_JPEG_VALUE
                }
                "jpeg" -> {
                    mimeType = MediaType.IMAGE_JPEG_VALUE
                }
                "png" -> {
                    mimeType = MediaType.IMAGE_PNG_VALUE
                }
                "pdf" -> {
                    mimeType = MediaType.APPLICATION_PDF_VALUE
                }
                "xls" -> {
                    mimeType = "application/vnd.ms-excel"
                }
                "xlsx" -> {
                    mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                }
                "zip" -> {
                    mimeType = "application/zip"
                }
                "rar" -> {
                    mimeType = "application/vnd.rar"
                }
                "doc" -> {
                    mimeType = "application/msword"
                }
                "docx" -> {
                    mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                }
            }

            amazonS3!!.setObjectAcl(splitChar[2], key, CannedAccessControlList.PublicRead)

            val fileStream: InputStream = URL("$storageUrl/$urlKey").openStream()
            response.contentType = mimeType
            IOUtils.copy(fileStream, response.outputStream)
            response.flushBuffer()
        }catch (e: AmazonS3Exception){
            println(e.message.toString())
        }catch (e: IOException){
            val out = response.writer
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            out.print("File not found")
            out.flush()
        }catch (e: FileNotFoundException){
            println(e.message.toString())
        }
    }
}