package xyz.dvnlabs.approval

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.core.data.local.LocalDB
import xyz.dvnlabs.approval.core.data.local.User
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.databinding.ActivityMainBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.LoginResponse
import xyz.dvnlabs.approval.view.activity.MenuActivity

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    private val userRepo: UserRepo by inject()
    private val localDB: LocalDB by inject()
    private val preferences: Preferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        lifecycleScope.launch {
            if (localDB.userDAO().getAllUser()?.isNotEmpty() == true) {
                val intent = Intent(this@MainActivity, MenuActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                setContentView(activityMainBinding.root)
                initView()
            }
        }
    }

    private fun initView() {
        activityMainBinding.mainButtonLogin.setOnClickListener {
            userRepo.login(
                this@MainActivity,
                activityMainBinding.mainTextfUsername.text.toString(),
                activityMainBinding.mainTextfPassword.text.toString(),
                object : BaseNetworkCallback<LoginResponse> {
                    override fun onSuccess(data: LoginResponse) {
                        lifecycleScope.launch {
                            val user = User(
                                token = data.token,
                                userName = data.username
                            )
                            localDB.userDAO()
                                .save(
                                    user
                                )
                            preferences.savePreference(
                                user
                            )
                        }
                        Toast.makeText(
                            this@MainActivity,
                            "Welcome ${data.username}",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@MainActivity, MenuActivity::class.java)
                        startActivity(intent)
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