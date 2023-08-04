package com.example.gasytravel.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetPostsModel(
    @SerializedName("maxPage")
    @Expose
    val maxPage: Int,
    @SerializedName("docs")
    @Expose
    val docs: ArrayList<Post>
)

data class Post(
    @SerializedName("_id")
    @Expose
    public val id: String?,

    @SerializedName("titre")
    @Expose
    public val titre: String,

    @SerializedName("Description")
    @Expose
    public val description: String,

    @SerializedName("Prix")
    @Expose
    public val prix: Double?,

    @SerializedName("Unite")
    @Expose
    public val unite: String?,

    @SerializedName("brand")
    @Expose
    public val brand: String,

    @SerializedName("Type")
    @Expose
    public val type: String
)