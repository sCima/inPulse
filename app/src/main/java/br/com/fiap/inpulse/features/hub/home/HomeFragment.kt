package br.com.fiap.inpulse.features.hub.home

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.response.IdeiaResponse
import br.com.fiap.inpulse.data.model.response.ProgramaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IdeaAdapter
    private lateinit var recyclerViewP: RecyclerView
    private lateinit var adapterP: ProgramaAdapter
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var programasVisible = false

    private lateinit var searchEditText: EditText
    private lateinit var searchIcon: ImageView

    private var originalIdeias: List<IdeiaResponse> = emptyList()
    private var originalProgramas: List<ProgramaResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInclude = view.findViewById<View>(R.id.search_idea)
        searchEditText = searchInclude.findViewById(R.id.editTextSearch)
        searchIcon = searchInclude.findViewById(R.id.searchIcon)

        recyclerView = view.findViewById(R.id.recyclerViewHome)
        recyclerView.setHasFixedSize(false)
        adapter = IdeaAdapter(mutableListOf(), "HomeFragment", viewLifecycleOwner)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        recyclerViewP = view.findViewById(R.id.recyclerViewHomeP)
        recyclerViewP.setHasFixedSize(false)
        adapterP = ProgramaAdapter(mutableListOf(),  "HomeFragment", viewLifecycleOwner)
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
            applySearchFilter(searchEditText.text.toString())
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applySearchFilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        searchIcon.setOnClickListener {
            applySearchFilter(searchEditText.text.toString())
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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
                    val ideiasFetched = RetrofitClient.inPulseApiService.loadIdeias()
                    val sortedIdeias = ideiasFetched.sortedByDescending { ideaResponse ->
                        try {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            dateFormat.parse(ideaResponse.data)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Date(0)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        if (isRefreshing) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            loadingProgressBar.visibility = View.GONE
                        }

                        originalIdeias = sortedIdeias

                        if (originalIdeias.isNotEmpty()) {
                            adapter.ideas.clear()
                            adapter.ideas.addAll(originalIdeias)
                            adapter.notifyDataSetChanged()

                            applySearchFilter(searchEditText.text.toString())
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
                    val programasFetched = RetrofitClient.inPulseApiService.loadProgramas()
                    withContext(Dispatchers.Main) {
                        if (isRefreshing) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            loadingProgressBar.visibility = View.GONE
                        }

                        originalProgramas = programasFetched

                        if (originalProgramas.isNotEmpty()) {
                            adapterP.programas.clear()
                            adapterP.programas.addAll(originalProgramas)
                            adapterP.notifyDataSetChanged()

                            applySearchFilter(searchEditText.text.toString())
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

    private fun applySearchFilter(query: String) {
        val lowerCaseQuery = query.lowercase(Locale.getDefault())

        if (programasVisible) {
            val filteredList = originalProgramas.filter {
                it.nome_programa.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.descricao_programa.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.funcionarios_nome.toString().lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }
            adapterP.programas.clear()
            adapterP.programas.addAll(filteredList)
            adapterP.notifyDataSetChanged()
        } else {
            val filteredList = originalIdeias.filter {
                it.nome.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.problema.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.descricao.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.funcionario_nome.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        it.categoriasIcone.toString().lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }
            adapter.ideas.clear()
            adapter.ideas.addAll(filteredList)
            adapter.notifyDataSetChanged()
        }
    }
}