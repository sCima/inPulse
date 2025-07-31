package br.com.fiap.inpulse.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.RetrofitClient
import br.com.fiap.inpulse.viewmodel.IdeaAdapter
import br.com.fiap.inpulse.viewmodel.ProgramaAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IdeaAdapter
    private lateinit var recyclerViewP: RecyclerView
    private lateinit var adapterP: ProgramaAdapter
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var programasVisible = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewHome)
        recyclerView.setHasFixedSize(false)
        adapter = IdeaAdapter(mutableListOf(), "HomeFragment", viewLifecycleOwner)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        recyclerViewP = view.findViewById(R.id.recyclerViewHomeP)
        recyclerViewP.setHasFixedSize(false)
        adapterP = ProgramaAdapter(mutableListOf())
        recyclerViewP.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewP.adapter = adapterP
        recyclerViewP.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        val btnIdeiasProgramas: TextView = view.findViewById(R.id.btn_ideias_programas)

        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshPage()
        }

        recyclerViewP.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE

        loadIdeiasFromApi()
        loadProgramasFromApi()

        btnIdeiasProgramas.setOnClickListener {
            programasVisible = !programasVisible
            if (programasVisible) {
                recyclerView.visibility = View.GONE
                recyclerViewP.visibility = View.VISIBLE
                recyclerViewP.scheduleLayoutAnimation()
                btnIdeiasProgramas.text = "Programas de inovação"
            } else {
                recyclerView.visibility = View.VISIBLE
                recyclerViewP.visibility = View.GONE
                recyclerView.scheduleLayoutAnimation()
                btnIdeiasProgramas.text = "Ideias"
            }
        }
    }

    private fun refreshPage() {
        if (programasVisible) {
            loadProgramasFromApi(isRefreshing = true)
        } else {
            loadIdeiasFromApi(isRefreshing = true)
        }
    }

    private fun loadIdeiasFromApi(isRefreshing: Boolean = false) {

        if (!isRefreshing) {
            loadingProgressBar.visibility = View.VISIBLE
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val ideias = RetrofitClient.inPulseApiService.loadIdeias()
                    withContext(Dispatchers.Main) {
                        if (isRefreshing) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            loadingProgressBar.visibility = View.GONE
                        }

                        if (ideias.isNotEmpty()) {
                            adapter.ideas.clear()
                            adapter.ideas.addAll(ideias)
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(context, "Nenhuma ideia encontrada.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        if (isRefreshing) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            loadingProgressBar.visibility = View.GONE
                        }
                        Toast.makeText(context, "Falha ao carregar ideias: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun loadProgramasFromApi(isRefreshing: Boolean = false) {

        if (!isRefreshing) {
            loadingProgressBar.visibility = View.VISIBLE
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val programas = RetrofitClient.inPulseApiService.loadProgramas()
                    withContext(Dispatchers.Main) {
                        if (isRefreshing) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            loadingProgressBar.visibility = View.GONE
                        }

                        if (programas.isNotEmpty()) {
                            adapterP.programas.clear()
                            adapterP.programas.addAll(programas)
                            adapterP.notifyDataSetChanged()
                        } else {
                            Toast.makeText(context, "Nenhum programa encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        if (isRefreshing) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            loadingProgressBar.visibility = View.GONE
                        }
                        Toast.makeText(context, "Falha ao carregar programas: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}