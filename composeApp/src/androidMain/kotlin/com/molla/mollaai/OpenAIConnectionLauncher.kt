package com.molla.mollaai

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * OpenAI 공식 API 키 발급 페이지를 Android 브라우저로 여는 헬퍼입니다.
 * 키는 앱이 가져오지 않고, 사용자가 직접 생성해서 입력하는 흐름을 유지합니다.
 */
object OpenAIConnectionLauncher {
    private const val OPENAI_API_KEYS_URL = "https://platform.openai.com/api-keys"

    fun openApiKeyPage(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(OPENAI_API_KEYS_URL))
        runCatching {
            context.startActivity(intent)
        }.onFailure {
            if (it is ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "브라우저를 찾을 수 없어요. OpenAI 페이지를 열 수 없습니다.",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "OpenAI 페이지를 여는 데 실패했습니다.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}
