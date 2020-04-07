package com.leaf.rxandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.leaf.rxandroid.fragments.PollingFragment

class FragmentTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = PollingFragment()

        fragmentTransaction.add(android.R.id.content, fragment)
        fragmentTransaction.commit()
    }
}