package com.example.gasytravel.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UploadBodyModel(
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("file")
    @Expose
    val file: String
)
data class UploadResponseModel(
    @SerializedName("message")
    @Expose
    val message: String,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("type")
    @Expose
    val type: String,

    @SerializedName("downloadURL")
    @Expose
    val downloadURL: String
)

data class ShowModel(
    @SerializedName("page")
    @Expose
    val page: Int,
    @SerializedName("pages")
    @Expose
    val pages: Int,
    @SerializedName("tv_shows")
    @Expose
    val tvShows: ArrayList<TvShow>
)

data class TvShow(
    @SerializedName("id")
    @Expose
    public val id: Int,
    @SerializedName("name")
    @Expose
    public val name: String,
    @SerializedName("status")
    @Expose
    public val status: String,
    @SerializedName("image_thumbnail_path")
    @Expose
    public val imageThumbnailPath: String
)