package com.example.wildrunning

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildrunning.LoginActivity.Companion.providerSession
import com.example.wildrunning.LoginActivity.Companion.useremail
import com.example.wildrunning.Utility.animateViewofFloat
import com.example.wildrunning.Utility.animateViewofInt
import com.example.wildrunning.Utility.getSecFromMatch
import com.example.wildrunning.Utility.setHeightLinearLayout
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import me.tankery.lib.circularseekbar.CircularSeekBar

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    private lateinit var csbChallengeDistance: CircularSeekBar
    private lateinit var csbCurrentDistance: CircularSeekBar
    private lateinit var csbRecordDistance: CircularSeekBar

    private lateinit var csbCurrentAvqSpeed: CircularSeekBar
    private lateinit var csbRecordAvqSpeed: CircularSeekBar

    private lateinit var csbCurrentSpeed: CircularSeekBar
    private lateinit var csbCurrentMaxSpeed: CircularSeekBar
    private lateinit var csbRecordSpeed: CircularSeekBar

    private lateinit var tvDistanceRecord: TextView
    private lateinit var tvAvgSpeedRecord: TextView
    private lateinit var tvMaxSpeedRecord: TextView

    private lateinit var swIntervalMode: Switch
    private lateinit var swChallenges: Switch
    private lateinit var swVolumes: Switch

    private lateinit var npChallengeDistance: NumberPicker
    private lateinit var npChallengeDurationHH: NumberPicker
    private lateinit var npChallengeDurationMM: NumberPicker
    private lateinit var npChallengeDurationSS: NumberPicker

    private var challengeDistance: Float = 0f
    private var challengeDuration: Int = 0

    private lateinit var tvChrono: TextView

    private lateinit var npDurationInterval: NumberPicker
    private lateinit var tvRunningTime: TextView
    private lateinit var tvWalkingTime: TextView

    private lateinit var csbRunWalk: CircularSeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initObjects()
        initToolBar()
        initNavigationView()
    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            signOut()
        }
    }

    private fun initStopWatch(){
        tvChrono.text = getString(R.string.init_stop_watch_value)
    }

    private fun initObjects(){

        tvChrono = findViewById(R.id.tvChrono)
        tvChrono.setTextColor(ContextCompat.getColor(this,R.color.white))
        initStopWatch()

        var lyMap = findViewById<LinearLayout>(R.id.lyMap)
        var lyFragmentMap=findViewById<LinearLayout>(R.id.lyFragmentMap)

        setHeightLinearLayout(lyMap,0)
        lyFragmentMap.translationY= -300f

        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lyIntervalMode= findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyChallengesSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenges = findViewById<LinearLayout>(R.id.lyChallenges)
        val lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolumes = findViewById<LinearLayout>(R.id.lySettingsVolumes)
        var lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        var lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)

        setHeightLinearLayout(lySoftTrack,0)
        setHeightLinearLayout(lySoftVolume,0)
        setHeightLinearLayout(lyMap,0)
        setHeightLinearLayout(lyIntervalModeSpace,0)
        setHeightLinearLayout(lyChallengesSpace,0)
        setHeightLinearLayout(lySettingsVolumesSpace,0)

        lyFragmentMap.translationY = -300f
        lyIntervalMode.translationY = -300f
        lyChallenges.translationY = -300f
        lySettingsVolumes.translationY = -300f

        csbCurrentDistance = findViewById(R.id.csbCurrentDistance)

        swIntervalMode=findViewById(R.id.swIntervalMode)
        swChallenges=findViewById(R.id.swChallenges)
        swVolumes=findViewById(R.id.swVolumes)

        npDurationInterval= findViewById(R.id.npDurationInterval)
        tvRunningTime = findViewById(R.id.tvRunningTime)
        tvWalkingTime = findViewById(R.id.tvWalkingTime)
        csbRunWalk = findViewById(R.id.csbRunWalk)

        npChallengeDistance = findViewById(R.id.npChallengeDistance)
        npChallengeDurationHH = findViewById(R.id.npChallengeDurationHH)
        npChallengeDurationMM = findViewById(R.id.npChallengeDurationMM)
        npChallengeDurationSS = findViewById(R.id.npChallengeDurationSS)

        csbRunWalk.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar,progress: Float,fromUser: Boolean) {

                var STEPS_UX: Int = 15
                var set: Int = 0
                var p = progress.toInt()

                if (p%STEPS_UX != 0){
                    while (p >= 60) p -= 60
                    while (p >= STEPS_UX) p -= STEPS_UX
                    if (STEPS_UX-p > STEPS_UX/2) set = -1 * p
                    else set = STEPS_UX-p

                    csbRunWalk.progress = csbRunWalk.progress + set
                }
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar) {
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar) {
            }
        })
    }

    private fun initToolBar(){
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer=findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer,toolbar,R.string.bar_title,R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun initNavigationView(){
        var navigationView: NavigationView=findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        var headerView: View=LayoutInflater.from(this).inflate(R.layout.nav_header_main,navigationView,false)
        navigationView.removeHeaderView(headerView)
        navigationView.addHeaderView(headerView)

        var tvUser: TextView = headerView.findViewById(R.id.tvUser)
        tvUser.text = useremail

    }

    fun callSignOut(view: View){
        signOut()
    }

    private fun signOut(){
        useremail=""

        if(providerSession =="Facebook") LoginManager.getInstance().logOut()

        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_record -> callRecordActivity()
            R.id.nav_item_signout -> signOut()
        }
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun callRecordActivity(){
        val intent = Intent(this,RecordActivity::class.java)
        startActivity(intent)
    }

    fun inflateIntervalMode(v: View){
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        var lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        var lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)
        var tvRounds = findViewById<TextView>(R.id.tvRounds)

        if(swIntervalMode.isChecked){
            animateViewofInt(swIntervalMode,"textColor",ContextCompat.getColor(this,R.color.orange),500)
            setHeightLinearLayout(lyIntervalModeSpace,600)
            animateViewofFloat(lyIntervalMode,"translationY",0f,500)
            animateViewofFloat(tvChrono,"translationX",-110f,500)
            tvRounds.setText(R.string.rounds)
            animateViewofInt(tvRounds,"textColor",ContextCompat.getColor(this,R.color.white),500)

            setHeightLinearLayout(lySoftTrack,120)
            setHeightLinearLayout(lySoftVolume,200)
            if(swVolumes.isChecked){
                 var lySettingsVolumesSpace= findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
                setHeightLinearLayout(lySettingsVolumesSpace,600)
            }
        }else {
            swIntervalMode.setTextColor(ContextCompat.getColor(this,R.color.white))
            setHeightLinearLayout(lyIntervalModeSpace,0)
            lyIntervalMode.translationY=-200f
            animateViewofFloat(tvChrono,"translationX",0f,500)
            tvRounds.text=""
            setHeightLinearLayout(lySoftTrack,0)
            setHeightLinearLayout(lySoftVolume,0)
            if(swVolumes.isChecked){
                var lySettingsVolumesSpace= findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
                setHeightLinearLayout(lySettingsVolumesSpace,400)
            }
        }
    }

    fun inflateChallenges(v: View){
        val lyChallengesSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenges = findViewById<LinearLayout>(R.id.lyChallenges)

        if(swChallenges.isChecked){
            animateViewofInt(swChallenges,"textColor",ContextCompat.getColor(this,R.color.orange),500)
            setHeightLinearLayout(lyChallengesSpace,750)
            animateViewofFloat(lyChallenges,"translationY",0f,500)
        }
        else{
            swChallenges.setTextColor(ContextCompat.getColor(this,R.color.white))
            setHeightLinearLayout(lyChallengesSpace,0)
            lyChallenges.translationY = -300f

            challengeDistance = 0f
            challengeDuration = 0
        }
    }

    fun showDuration(v: View){
        showChallenge("duration")
    }

    fun showDistance(v: View){
        showChallenge("distance")
    }

    private fun showChallenge(option: String){
        var lyChallengeDistance = findViewById<LinearLayout>(R.id.lyChallengeDistance)
        var lyChallengeDuration = findViewById<LinearLayout>(R.id.lyChallengeDuration)
        var tvChallengeDistance = findViewById<TextView>(R.id.tvChallengeDistance)
        var tvChallengeDuration = findViewById<TextView>(R.id.tvChallengeDuration)

        when(option){
            "duration"-> {
                lyChallengeDistance.translationZ = 0f
                lyChallengeDuration.translationZ = 5f

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this,R.color.white))
                tvChallengeDistance.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_medium))

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this,R.color.orange))
                tvChallengeDuration.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_dark))

                challengeDistance=0f
                getChallengeDuration(npChallengeDurationHH.value,npChallengeDurationMM.value,npChallengeDurationSS.value)
            }
            "distance"->{
                lyChallengeDistance.translationZ=5f
                lyChallengeDuration.translationZ=0f

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this,R.color.orange))
                tvChallengeDistance.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_dark))

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this,R.color.white))
                tvChallengeDuration.setBackgroundColor(ContextCompat.getColor(this,R.color.gray_medium))

                challengeDuration = 0
                challengeDistance = npChallengeDistance.value.toFloat()
            }
        }
    }

    private fun getChallengeDuration(hh: Int,mm: Int,ss: Int){
        var hours: String = hh.toString()
        if(hh<10) hours = "0"+hours
        var minutes: String = mm.toString()
        if(mm<10) minutes = "0"+minutes
        var seconds: String = ss.toString()
        if(ss<10) seconds = "0"+seconds

        challengeDuration = getSecFromMatch("${hours}:${minutes}:${seconds}")
    }

    fun inflateVolumes(v: View){
        val lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolumes = findViewById<LinearLayout>(R.id.lySettingsVolumes)

        if(swVolumes.isChecked){
            animateViewofInt(swVolumes,"textColor",ContextCompat.getColor(this,R.color.orange),500)
            var swIntervalMode = findViewById<Switch>(R.id.swIntervalMode)
            var value = 400
            if(swIntervalMode.isChecked){
                value=600
            }
            setHeightLinearLayout(lySettingsVolumesSpace,value)
            animateViewofFloat(lySettingsVolumes,"translationY",0f,500)
        }else{
            swVolumes.setTextColor(ContextCompat.getColor(this,R.color.white))
            setHeightLinearLayout(lySettingsVolumesSpace,0)
            lySettingsVolumes.translationY = -300f
        }
    }
}