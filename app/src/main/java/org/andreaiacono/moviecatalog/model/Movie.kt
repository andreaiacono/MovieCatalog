package org.andreaiacono.moviecatalog.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.util.*


@JacksonXmlRootElement(localName = "details")
class Details (

    @JacksonXmlProperty(localName = "movie")
    val movie: Movie
)



@JacksonXmlRootElement(localName = "movie")
class Movie (

    @JacksonXmlProperty(localName = "title")
    val title: String,

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "dd/MM/yyyy"
    )
    @JacksonXmlProperty(localName = "date")
    val date: Date,

    val dirName: String = ""
)