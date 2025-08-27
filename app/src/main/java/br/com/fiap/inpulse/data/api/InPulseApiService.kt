package br.com.fiap.inpulse.data.api

import br.com.fiap.inpulse.data.model.request.ContribuicaoRequest
import br.com.fiap.inpulse.data.model.request.FuncionarioIdRequest
import br.com.fiap.inpulse.data.model.request.FuncionarioRequest
import br.com.fiap.inpulse.data.model.request.IdeiaIdRequest
import br.com.fiap.inpulse.data.model.request.IdeiaRequest
import br.com.fiap.inpulse.data.model.request.LikeRequest
import br.com.fiap.inpulse.data.model.request.SenhaRequest
import br.com.fiap.inpulse.data.model.request.UpdateStatsRequest
import br.com.fiap.inpulse.data.model.response.ContribuicaoResponse
import br.com.fiap.inpulse.data.model.response.FuncionarioResponse
import br.com.fiap.inpulse.data.model.response.IdeiaResponse
import br.com.fiap.inpulse.data.model.response.ProgramaResponse
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

    @POST("ideias")
    suspend fun sendIdeia(@Body requestBody: IdeiaRequest): IdeiaResponse

    @POST("contribuicoes")
    suspend fun sendContribuicao(@Body requestBody: ContribuicaoRequest): ContribuicaoResponse

    //programas
    @GET("programas")
    suspend fun loadProgramas(): List<ProgramaResponse>

    @PUT("programas/funcionarios/{id}")
    suspend fun subscribePrograma(@Path("id") programaId: Int, @Body requestBody: FuncionarioIdRequest): ProgramaResponse

    @PUT("programas/ideias/{id}")
    suspend fun subscribeIdeia(@Path("id") programaId: Int, @Body requestBody: IdeiaIdRequest): ProgramaResponse

    //funcionarios
    @GET("funcionarios")
    suspend fun loadFuncionarios(): List<FuncionarioResponse>

    @GET("funcionarios/email/{email}")
    suspend fun getFuncionarioByEmail(@Path("email") email: String): FuncionarioResponse

    @GET("funcionarios/{id}")
    suspend fun getFuncionarioById(@Path("id") id: Int): FuncionarioResponse

    @POST("funcionarios")
    suspend fun cadastrarFuncionario(@Body request: FuncionarioRequest): FuncionarioResponse

    @PUT("funcionarios/{id}")
    suspend fun updateFuncionarioStats(@Path("id") id: Int, @Body requestBody: UpdateStatsRequest): FuncionarioResponse

    @PUT("funcionarios/senha/{id}")
    suspend fun updateSenha(@Path("id") id: Int, @Body requestBody: SenhaRequest): FuncionarioResponse


}
