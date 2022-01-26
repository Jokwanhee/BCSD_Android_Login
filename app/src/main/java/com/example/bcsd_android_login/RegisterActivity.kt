package com.example.bcsd_android_login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.bcsd_android_login.databinding.ActivityRegisterBinding
import com.example.bcsd_android_login.model.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var account: UserAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("JoUserData")

        binding.registerButton.setOnClickListener {
            if (binding.editId.text.toString() == "") {
                Toast.makeText(this, "오류 : 이메일을 입력하시오", Toast.LENGTH_SHORT).show()
            } else if (binding.editPassword.text.toString() == "") {
                Toast.makeText(this, "오류 : 비밀번호를 입력하시오", Toast.LENGTH_SHORT).show()
            } else if (binding.editId.text.toString() != "" && binding.editPassword.text.toString() != "") {
                val strEmail = binding.editId.text.toString()
                val strPwd = binding.editPassword.text.toString()
                registerSuccess(strEmail, strPwd)
            }
        }
    }

    private fun registerSuccess(strEmail: String, strPwd: String) {
        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    account = UserAccount(
                        firebaseUser?.uid.toString(),
                        firebaseUser?.email.toString(),
                        strPwd
                    )

                    mDatabaseReference.child("UserAccount").child(firebaseUser?.uid.toString())
                        .setValue(account)

                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}