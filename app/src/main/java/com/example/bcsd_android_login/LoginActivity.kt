package com.example.bcsd_android_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.bcsd_android_login.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    // firebase 인증을 위한 변수
    private lateinit var firebaseAuth: FirebaseAuth
    // 구글 연동에 필요한 변수
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    // 구글 로그인 인증을 요청했을 때 결과 값을 되돌려 받는 곳
    private val startResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RC_SIGN_IN) {
                val task =
                    GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    resultLogIn(account!!)
                } catch (e: ApiException) {
                    Log.w("LoginActivity", "Google sign in failed", e)
                }
            }
        }

    private fun resultLogIn(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) { // 로그인 성공 시
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, GoogleLoadingActivity::class.java)
                    intent.putExtra("nickname", account.displayName)
                    intent.putExtra("photoUrl", account.photoUrl.toString())
                    startResult.launch(intent)
                } else { // 로그인 실패 시
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {
        this.onSignInResult(it)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Log.d("testing", "${user?.displayName}")
            Log.d("testing", "${user?.email}")
            val intent = Intent(this, GoogleLoadingActivity::class.java)
            intent.putExtra("nickname", user?.displayName)
            intent.putExtra("photoUrl", user?.photoUrl.toString())
            startResult.launch(intent)
            finish()
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    private fun signInUI() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//        AuthUI.IdpConfig.PhoneBuilder().build(),
//        AuthUI.IdpConfig.FacebookBuilder().build(),
//        AuthUI.IdpConfig.TwitterBuilder().build())
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // custom 로그인 방식
        binding.loginButton.setOnClickListener {
            val strEmail = binding.loginId.text.toString()
            val strPwd = binding.loginPassword.text.toString()
            firebaseAuth.signInWithEmailAndPassword(strEmail, strPwd)
                .addOnCompleteListener(this@LoginActivity){
                    if (it.isSuccessful) {
                        val intent = Intent(this, GoogleLoadingActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        // custom 회원가입 방식
        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign in (구글 로그인을 위해 구성되어야 하는 코드 (id, email request))
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("679966604367-am1042p6vpntnar8aom0oafchcfvl3aa.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // firebaseauth를 사용하기 위한 인스턴스 get
        firebaseAuth = FirebaseAuth.getInstance()

        // 구글 로그인 방식
        binding.signInButton.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startResult.launch(intent)
            signInUI()
        }
    }
}