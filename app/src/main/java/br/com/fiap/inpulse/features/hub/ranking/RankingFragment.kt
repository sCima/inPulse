package br.com.fiap.inpulse.features.hub.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.inpulse.R
import br.com.fiap.inpulse.data.api.RetrofitClient
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.data.model.response.IdeiaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankingFragment : Fragment() {

    private lateinit var adapterU: UserRankingAdapter
    private lateinit var adapterI: IdeaRankingAdapter
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var recyclerViewU: RecyclerView
    private lateinit var btnColaboradores: AppCompatButton
    private var listaDeFuncionarios = listOf<FuncionarioResponse>()
    private var listaDeIdeias = listOf<IdeiaResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        btnColaboradores = view.findViewById(R.id.btnColaboradores)
        recyclerViewU = view.findViewById(R.id.recyclerViewRankingUsers)
        val recyclerViewI = view.findViewById<RecyclerView>(R.id.recyclerViewRankingIdeas)
        val spinner: Spinner = view.findViewById(R.id.spinnerFiltro)

        adapterU = UserRankingAdapter(mutableListOf())
        recyclerViewU.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewU.adapter = adapterU

        adapterI = IdeaRankingAdapter(mutableListOf())
        recyclerViewI.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewI.adapter = adapterI

        val adapterSpinnerUsers = ArrayAdapter.createFromResource(
            requireContext(), R.array.filtros_array, android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val adapterSpinnerIdeas = ArrayAdapter.createFromResource(
            requireContext(), R.array.filtros_array_i, android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        btnColaboradores.setOnClickListener {
            if (recyclerViewU.visibility == View.VISIBLE) {
                recyclerViewI.visibility = View.VISIBLE
                recyclerViewU.visibility = View.GONE
                btnColaboradores.text = "Ideias"
                spinner.adapter = adapterSpinnerIdeas

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        val selectedItem = p0?.getItemAtPosition(position).toString()
                        val sortKey = when (selectedItem) {
                            "Contribuições" -> "contribuições"
                            "Programas" -> "programas"
                            else -> "curtidas"
                        }
                        adapterI.updateAndSortIdeas(listaDeIdeias, sortKey)
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

            } else {
                recyclerViewU.visibility = View.VISIBLE
                recyclerViewI.visibility = View.GONE
                btnColaboradores.text = "Colaboradores"
                spinner.adapter = adapterSpinnerUsers

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                        val selectedItem = p0?.getItemAtPosition(position).toString()
                        val sortKey = when (selectedItem) {
                            "Curtidas" -> "curtidas"
                            "Pontos" -> "pontos"
                            "Selos" -> "selos"
                            "Programas" -> "programas"
                            else -> "ideias"
                        }
                        adapterU.updateAndSortUsers(listaDeFuncionarios, sortKey)
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
            }
        }

        btnColaboradores.performClick()
        btnColaboradores.performClick()

        loadFuncionariosFromApi()
        loadIdeiasFromApi()
    }

    private fun loadIdeiasFromApi() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val ideias = RetrofitClient.inPulseApiService.loadIdeias()
                    withContext(Dispatchers.Main) {
                        if (ideias.isNotEmpty()) {
                            listaDeIdeias = ideias
                            adapterI.updateAndSortIdeas(listaDeIdeias, "curtidas")
                        } else {
                            Toast.makeText(context, "Nenhuma ideia encontrada.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Falha ao carregar ideias: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun loadFuncionariosFromApi() {
        loadingProgressBar.visibility = View.VISIBLE
        recyclerViewU.visibility = View.GONE
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val users = RetrofitClient.inPulseApiService.loadFuncionarios()
                    withContext(Dispatchers.Main) {
                        loadingProgressBar.visibility = View.GONE
                        recyclerViewU.visibility = View.VISIBLE
                        if (users.isNotEmpty()) {
                            listaDeFuncionarios = users
                            adapterU.updateAndSortUsers(listaDeFuncionarios, "ideias")
                        } else {
                            Toast.makeText(context, "Nenhum usuário encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        loadingProgressBar.visibility = View.GONE
                        recyclerViewU.visibility = View.VISIBLE
                        Toast.makeText(context, "Falha ao carregar usuários: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}
