package tn.rnu.isetr.projet_medical

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.databinding.ActivityMainBinding
import tn.rnu.isetr.projet_medical.ui.auth.LoginActivity
import tn.rnu.isetr.projet_medical.ui.home.HomeFragment
import tn.rnu.isetr.projet_medical.ui.mesrdv.MesRendezVousFragment
import tn.rnu.isetr.projet_medical.ui.search.SearchFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)

        binding.navView.setNavigationItemSelectedListener(this)
        val headerView = binding.navView.getHeaderView(0)
        val headerBinding = tn.rnu.isetr.projet_medical.databinding.NavHeaderBinding.bind(headerView)
        headerBinding.tvUserNom.text = sessionManager.getUserName() ?: "Nom du Patient"
        headerBinding.tvUserEmail.text = sessionManager.getUserEmail() ?: "patient@mail.com"

        // Bouton hamburger
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Flèche retour
        toggle.setToolbarNavigationClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val aUnEcranPrecedent = supportFragmentManager.backStackEntryCount > 0
            if (aUnEcranPrecedent) {
                toggle.isDrawerIndicatorEnabled = false
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                toggle.isDrawerIndicatorEnabled = true
            }
        }

        // 🟢 C'EST ICI : On lance HomeFragment par défaut au démarrage
        if (savedInstanceState == null) {
            binding.navView.setCheckedItem(R.id.nav_home) // Assure-toi que ton item s'appelle nav_home dans le menu (ou nav_accueil)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .commit()
        }

        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home -> { // 🟢 Clic sur Accueil dans le menu latéral
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HomeFragment())
                    .commit()
            }
            R.id.nav_recherche -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SearchFragment())
                    .commit()
            }
            R.id.nav_rdv -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, MesRendezVousFragment())
                    .commit()
            }
            R.id.nav_profil -> {
                // TODO: Charger le fragment profil
            }
            R.id.nav_logout -> {
                sessionManager.clear()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}