package com.example.finance

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var intent = intent
        if(intent.getBooleanExtra("passed", false)) {
            setContentView(R.layout.activity_main)
            var frag = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, frag)
                .commit()
        } else {
            intent = Intent(this@MainActivity, splash::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.settings -> {
                setContentView(R.layout.activity_main)
                loadSettings()
            }
            R.id.about -> {
                setContentView(R.layout.activity_main)
                loadAbout()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun loadSettings() {
        var frag = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
    fun loadAbout() {
        var frag = AboutFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
    fun loadDashboard(v: View) {
        var frag = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
    fun loadTransaction(v: View) {
        var frag = TransactionFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
    fun loadManageBudgets(v: View) {
        var frag = ManageFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
    fun loadHistory(v: View) {
        var frag = HistoryFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, frag)
            .commit()
    }
}