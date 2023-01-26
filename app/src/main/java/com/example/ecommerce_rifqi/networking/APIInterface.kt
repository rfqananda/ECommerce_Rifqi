package com.example.ecommerce_rifqi.networking

import com.example.ecommerce_rifqi.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIInterface {

    @FormUrlEncoded
    @POST("api/ecommerce/authentication")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginSuccess>

    @Multipart
    @POST("api/ecommerce/registration")
    fun register(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part image: MultipartBody.Part,
        ) : Call<RegisterSuccess>

    @FormUrlEncoded
    @PUT("api/ecommerce/change-password/{id_user}")
    fun changePassword(
        @Path("id_user") username: Int,
        @Field("password") password: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String) : Call<ChangePassSuccess>

    @Multipart
    @POST("api/ecommerce/change-image")
    fun changeImage(
        @Part("id") password: RequestBody,
        @Part image: MultipartBody.Part,
    ) : Call<ChangeImageSuccess>


    @FormUrlEncoded
    @Headers(*["apikey:TuIBt77u7tZHi8n7WqUC"])
    @POST("api/ecommerce/refresh-token")
    suspend fun refreshToken(
        @Field("id_user") id_user: Int,
        @Field("access_token") access_token: String?,
        @Field("refresh_token") refresh_token: String?
    ) : Response<ResponseRefreshToken>


    @GET("api/ecommerce/get_list_product")
    fun getListProduct(
        @Query("search") search: String
    ): Call<ListDataProduct>

    @GET("api/ecommerce/get_list_product_favorite")
    fun getFavorite(
        @Query("search") search : String?,
        @Query("id_user") id_user : Int
    ) : Call<ListDataProduct>


    @GET("api/ecommerce/get_detail_product")
    fun getDetailProduct(
        @Query("id_product") id_product: Int,
        @Query("id_user") id_user : Int
    ): Call<ResponseDetailProduct>

    @FormUrlEncoded
    @POST("api/ecommerce/add_favorite")
    fun addToFav(
        @Field("id_product") id_product: Int,
        @Field("id_user") id_user: Int
        ): Call<AddToFavoriteSuccess>

    @FormUrlEncoded
    @POST("api/ecommerce/remove_favorite")
    fun removeFav(
        @Field("id_product") id_product: Int,
        @Field("id_user") id_user: Int
    ): Call<RemoveFromFavoriteSuccess>

    @POST("api/ecommerce/update-stock")
    fun updateStock(
        @Body dataStock: DataStock
    ): Call<UpdateStockSuccess>

    @FormUrlEncoded
    @PUT("api/ecommerce/update_rate/{id_product}")
    fun updateRate(
        @Path("id_product") id_product: Int?,
        @Field("rate") rate: Int
    ) : Call<UpdateRateSuccess>

}