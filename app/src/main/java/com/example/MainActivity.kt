package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.HRAppContent
import com.example.ui.viewmodel.HRViewModel

class MainActivity : ComponentActivity() {
    private val hrViewModel: HRViewModel by viewModels {
        HRViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Turn on beautiful full edge-to-edge rendering
        enableEdgeToEdge()
        
        setContent {
            HRAppContent(viewModel = hrViewModel)
        }
    }
}
