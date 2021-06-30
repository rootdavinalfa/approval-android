package xyz.dvnlabs.approval

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.databinding.ActivityMainBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.LoginResponse

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    private val userRepo: UserRepo by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initView()
    }

    private fun initView() {
        activityMainBinding.mainButtonLogin.setOnClickListener {
            userRepo.login(
                activityMainBinding.mainTextfUsername.text.toString(),
                activityMainBinding.mainTextfPassword.text.toString(),
                object : BaseNetworkCallback<LoginResponse> {
                    override fun onSuccess(data: LoginResponse) {
                        Toast.makeText(this@MainActivity, "Login Success", Toast.LENGTH_LONG).show()
                    }

                    override fun onFailed(errorResponse: ErrorResponse) {
                        Toast.makeText(
                            this@MainActivity,
                            errorResponse.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onUnAuthorized(errorResponse: ErrorResponse) {
                        Toast.makeText(
                            this@MainActivity,
                            errorResponse.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onShowProgress() {
                        println()
                    }

                    override fun onHideProgress() {
                        println()
                    }

                }
            )
        }
    }
}