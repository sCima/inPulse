package br.com.fiap.inpulse.data

import br.com.fiap.inpulse.model.response.FuncionarioResponse
import br.com.fiap.inpulse.model.response.IdeiaResponse
import br.com.fiap.inpulse.model.response.ProgramaResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface InPulseApiService {

    //ideias
    @GET("ideias")
    suspend fun loadIdeias(): List<IdeiaResponse>

    //programas
    @GET("programas")
    suspend fun loadProgramas(): List<ProgramaResponse>

    //funcionarios
    @GET("funcionarios/email/{email}")
    suspend fun getFuncionarioByEmail(@Path("email") email: String): FuncionarioResponse

}
