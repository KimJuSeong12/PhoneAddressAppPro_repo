package com.example.phoneaddressapppro

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.phoneaddressapppro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 1. 주소록을가져오기 위해서 사용자에게 퍼미션 허용했는지 확인
        val status = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")
        if (status == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "주소록앱 정보 가져오기 요청 허락됨", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "주소록앱 정보 가져오기 요청이 안되어 있음", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>("android.permission.READ_CONTACTS"),
                100
            )
        }

        // 2. 주소록 정보를 요청했을 때 주소록앱에서 선택된 주소 Uri 보내줄 때 받을 콜백함수
        val requestLaucher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    // 컨텐트 프로바이더를 통해서 데이터를 가져온다.
                    val cursor = contentResolver.query(
                        it.data!!.data!!,
                        arrayOf<String>(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        ), null, null, null
                    )
                    if (cursor!!.moveToNext()) {
                        val name = cursor.getString(0)
                        val phone = cursor.getString(1)
                        binding.textView.text = "name = ${name}, phone = ${phone}"
                    }
                }
            }

        // 3. 이벤트 처리 (주소록앱 Activitiy 인텐트를 통해서 부른다. -> 주소록앱을 클릭하면 주소록 Uri 돌려준다.)
        binding.button.setOnClickListener {
            // 주소록에서 전화번호앱 리스트를 전화번호를 가지고 있는 사람만 리사이클러뷰로 보여달라고 요청
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            requestLaucher.launch(intent)
        }
    } // end of onCreate
}