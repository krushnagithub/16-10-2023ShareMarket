package com.example.a16_10_2023sharemarket.view

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a16_10_2023sharemarket.R
import com.example.a16_10_2023sharemarket.databinding.FragmentDashaboardBinding
import kotlin.random.Random

class DashaboardFragment : Fragment() {
    private lateinit var binding: FragmentDashaboardBinding
    private lateinit var navController: NavController
    private lateinit var ShareHoldingList: ArrayList<ShareHolding>
    private lateinit var holdingAdapter: HoldingAdapter

    private val handler = Handler(Looper.getMainLooper())

    private val updateInterval = 1000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashaboardBinding.bind(inflater.inflate(R.layout.fragment_dashaboard, null))
        setUpBackPressHandler()
        setUpListeners()
        initData()
        initView()
        startIndexUpdates()

        return binding.root
    }

    private fun initData() {
        ShareHoldingList = ArrayList()

        ShareHoldingList.add(ShareHolding("Bmw", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Bajaj Finance", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Airtel", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Idea", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Adani Port", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Tata Motor", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Zomato", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Swiggy", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("MRF Tyres", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Tyres", "13-10-2023", "13-11-2023", 20.1))
        ShareHoldingList.add(ShareHolding("Adani Power", "13-10-2023", "13-11-2023", 20.1))
    }

    private fun initView() {
        binding.shareHoldingRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        holdingAdapter = HoldingAdapter(ShareHoldingList)
        binding.shareHoldingRecyclerView.adapter = holdingAdapter
        holdingAdapter.notifyDataSetChanged()
    }

    private fun setUpListeners() {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
    }

    private fun setUpBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun startIndexUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val bseIndex = Random.nextDouble(50000.0, 70000.0)
                val nseIndex = Random.nextDouble(12000.0, 15000.0)

                binding.txtBseIndexValue.text = String.format("%.2f", bseIndex)
                binding.txtNseIndexValue.text = String.format("%.2f", nseIndex)

                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }
}
