package br.com.fiap.inpulse.data

import br.com.fiap.inpulse.data.request.ContribuicaoRequest
import br.com.fiap.inpulse.data.request.FuncionarioRequest
import br.com.fiap.inpulse.data.request.LikeRequest
import br.com.fiap.inpulse.data.response.Contribuicao
import br.com.fiap.inpulse.data.response.ContribuicaoResponse
import br.com.fiap.inpulse.data.response.FuncionarioResponse
import br.com.fiap.inpulse.data.response.IdeiaResponse
import br.com.fiap.inpulse.data.response.ProgramaResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InPulseApiService {

    //ideias
    @GET("ideias")
    suspend fun loadIdeias(): List<IdeiaResponse>

    @PUT("ideias/{id}")
    suspend fun updateIdeia(@Path("id") id: Int, @Body requestBody: LikeRequest): IdeiaResponse

    @POST("contribuicoes")
    suspend fun sendContribuicao(@Body requestBody: ContribuicaoRequest): ContribuicaoResponse

    //programas
    @GET("programas")
    suspend fun loadProgramas(): List<ProgramaResponse>

    //funcionarios
    @GET("funcionarios")
    suspend fun loadFuncionarios(): List<FuncionarioResponse>

    @GET("funcionarios/email/{email}")
    suspend fun getFuncionarioByEmail(@Path("email") email: String): FuncionarioResponse

    @GET("funcionarios/{id}")
    suspend fun getFuncionarioById(@Path("id") id: Int): FuncionarioResponse

    @POST("funcionarios")
    suspend fun cadastrarFuncionario(@Body request: FuncionarioRequest): FuncionarioResponse

}
