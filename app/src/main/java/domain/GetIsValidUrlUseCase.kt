package domain

import android.util.Patterns
import javax.inject.Inject

class GetIsValidUrlUseCase @Inject constructor() {

    fun run(fileUrl: String): Boolean {
        return Patterns.WEB_URL.matcher(fileUrl).matches()
    }
}