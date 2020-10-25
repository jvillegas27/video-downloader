package app.permission

import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import io.reactivex.Single
import javax.inject.Inject

class PermissionsManagerImpl @Inject constructor(private val realRxPermission: RealRxPermission) :
    PermissionsManager {

    override fun request(permission: String): Single<Permission> {
        return realRxPermission.request(permission)
    }
}