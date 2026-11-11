package com.grupo07.pokedex

import android.graphics.Bitmap
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.grupo07.pokedex.userinterface.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

/**
 * Percorre o app como o professor vai testar e salva prints das telas.
 * Roda no emulador (GitHub Actions): precisa de internet.
 */
@RunWith(AndroidJUnit4::class)
class RoteiroDoProfessorTest {

    @get:Rule
    val regra = createAndroidComposeRule<MainActivity>()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    private val emailA = "guilherme@grupo07.com"
    private val emailB = "joaopaulo@grupo07.com"
    private val senha = "pikachu123"

    @Test
    fun roteiroCompleto() {
        // 1. Criar uma conta
        regra.onNodeWithText("Cadastre-se").performClick()
        esperarTexto("Criar conta")
        preencherCadastro("Guilherme Lage", emailA)
        print("cadastro")
        regra.onNodeWithText("Cadastrar").performClick()

        // 2. Lista vinda da API
        esperarTexto("Bulbasaur", 30_000)
        print("lista")

        // 3. Detalhe + salvar no Room
        regra.onNodeWithText("Bulbasaur").performClick()
        esperarTexto("Atributos base", 30_000)
        regra.onNodeWithText("Adicionar aos Favoritos").performScrollTo().performClick()
        esperarTexto("Remover dos Favoritos")
        regra.onNodeWithText("Bulbasaur").performScrollTo()
        print("detalhe")
        regra.onNodeWithContentDescription("Voltar").performClick()

        // 4. Aba Favoritos
        esperarTexto("Bulbasaur")
        regra.onNodeWithText("Favoritos").performClick()
        esperarTexto("1 salvo")
        print("favoritos")

        // 5. Sair e tentar entrar com a senha errada
        sair()
        campos()[0].performTextInput(emailA)
        campos()[1].performTextInput("senhaerrada")
        Espresso.closeSoftKeyboard()
        regra.onNodeWithText("Entrar").performClick()
        esperarTexto("E-mail ou senha incorretos. Tente novamente.")
        print("login")

        // 6. Outra conta não enxerga os favoritos da primeira
        regra.onNodeWithText("Cadastre-se").performClick()
        esperarTexto("Criar conta")
        preencherCadastro("João Paulo", emailB)
        regra.onNodeWithText("Cadastrar").performClick()
        esperarTexto("Pokedex", 30_000)
        regra.onNodeWithText("Favoritos").performClick()
        esperarTexto("Nenhum favorito ainda")
        sair()

        // 7. Primeira conta continua com o favorito salvo
        campos()[0].performTextClearance()
        campos()[0].performTextInput(emailA)
        campos()[1].performTextClearance()
        campos()[1].performTextInput(senha)
        Espresso.closeSoftKeyboard()
        regra.onNodeWithText("Entrar").performClick()
        esperarTexto("Bulbasaur", 30_000)
        regra.onNodeWithText("Favoritos").performClick()
        esperarTexto("1 salvo")

        // 8. Modo avião: estado de erro
        shell("cmd connectivity airplane-mode enable")
        Thread.sleep(3_000)
        regra.onNodeWithText("Pokedex").performClick()
        esperarTexto("Ivysaur", 30_000)
        regra.onNodeWithText("Ivysaur").performClick()
        esperarTexto("Sem conexão", 30_000)
        print("erro")
        shell("cmd connectivity airplane-mode disable")
    }

    // ---------- ajudantes ----------

    private fun campos(): SemanticsNodeInteractionCollection =
        regra.onAllNodes(hasSetTextAction())

    private fun preencherCadastro(nome: String, email: String) {
        campos()[0].performTextInput(nome)
        Thread.sleep(500)
        campos()[1].performTextInput(email)
        Thread.sleep(500)
        campos()[2].performTextInput(senha)
        Thread.sleep(500)
        campos()[3].performTextInput(senha)
        Thread.sleep(500)
        Espresso.closeSoftKeyboard()
    }

    private fun sair() {
        regra.onNodeWithText("Perfil").performClick()
        esperarTexto("Sair da conta")
        regra.onNodeWithText("Sair da conta").performClick()
        esperarTexto("Entre para acessar sua conta")
    }

    private fun esperarTexto(texto: String, tempoMs: Long = 10_000) {
        regra.waitUntil(tempoMs) {
            regra.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
        Thread.sleep(1_200) // pausa para o vídeo da demonstração ficar acompanhável
    }

    private fun shell(comando: String) {
        instrumentation.uiAutomation.executeShellCommand(comando).close()
    }

    /** Tira um print da tela inteira e salva em files/prints/<nome>.png */
    private fun print(nome: String) {
        regra.waitForIdle()
        Thread.sleep(2_500) // dá tempo das imagens carregarem
        val bitmap = instrumentation.uiAutomation.takeScreenshot() ?: return
        val pasta = File(instrumentation.targetContext.getExternalFilesDir(null), "prints").apply { mkdirs() }
        FileOutputStream(File(pasta, "$nome.png")).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
