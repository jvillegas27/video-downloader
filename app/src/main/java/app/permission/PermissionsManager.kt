package app.permission

import com.vanniktech.rxpermission.Permission
import io.reactivex.Single

interface PermissionsManager {
    fun request(permission: String): Single<Permission>
}