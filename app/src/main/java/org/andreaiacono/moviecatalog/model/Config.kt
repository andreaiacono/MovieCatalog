package org.andreaiacono.moviecatalog.model

import com.fasterxml.jackson.annotation.JsonProperty


data class Config(

    @JsonProperty("nasUrl")
    var nasUrl: String,

    @JsonProperty("duneIp")
    var duneIp: String
)