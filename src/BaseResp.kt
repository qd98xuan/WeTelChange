package com.example

data class BaseResp<T>(
    val success:Boolean,
    val code:Int,
    val messgae:String,
    val result:T
)