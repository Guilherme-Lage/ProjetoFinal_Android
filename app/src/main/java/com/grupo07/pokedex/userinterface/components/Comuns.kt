package com.grupo07.pokedex.userinterface.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grupo07.pokedex.userinterface.theme.CinzaTexto
import com.grupo07.pokedex.userinterface.theme.Vermelho
import com.grupo07.pokedex.userinterface.theme.VermelhoClaro

/** Campo de texto com rótulo em cima, como no Figma. */
@Composable
fun CampoTexto(
    rotulo: String,
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    erro: String? = null,
    temErro: Boolean = erro != null,
    ajuda: String? = null,
    senha: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    acaoTeclado: ImeAction = ImeAction.Next
) {
    var mostrarSenha by rememberSaveable { mutableStateOf(false) }
    val textoApoio = erro ?: ajuda

    Column(modifier.fillMaxWidth()) {
        Text(rotulo, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            singleLine = true,
            isError = temErro,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (senha) KeyboardType.Password else tipoTeclado,
                imeAction = acaoTeclado
            ),
            visualTransformation =
                if (senha && !mostrarSenha) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (senha) {
                {
                    IconButton(onClick = { mostrarSenha = !mostrarSenha }) {
                        Icon(
                            if (mostrarSenha) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (mostrarSenha) "Esconder senha" else "Mostrar senha"
                        )
                    }
                }
            } else null,
            supportingText = if (textoApoio != null) {
                { Text(textoApoio) }
            } else null
        )
    }
}

/** Faixa vermelha clara com mensagem de erro (login com senha errada). */
@Composable
fun BannerErro(mensagem: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(VermelhoClaro, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Vermelho)
        Spacer(Modifier.size(8.dp))
        Text(mensagem, color = Vermelho, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Ícone dentro de um círculo vermelho claro (estados vazios e de erro). */
@Composable
fun IconeCirculo(icone: ImageVector) {
    Box(
        Modifier
            .size(88.dp)
            .background(VermelhoClaro, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icone, contentDescription = null, tint = Vermelho, modifier = Modifier.size(40.dp))
    }
}

/** Tela de erro com botão de tentar de novo. */
@Composable
fun EstadoErro(
    semConexao: Boolean,
    onTentarDeNovo: () -> Unit,
    modifier: Modifier = Modifier,
    rodape: String? = null
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconeCirculo(if (semConexao) Icons.Filled.WifiOff else Icons.Filled.ErrorOutline)
        Spacer(Modifier.height(16.dp))
        Text(
            if (semConexao) "Sem conexão" else "Algo deu errado",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (semConexao) "Não foi possível carregar os dados. Verifique sua internet e tente de novo."
            else "A PokéAPI não respondeu como esperado. Tente de novo em instantes.",
            color = CinzaTexto,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onTentarDeNovo, shape = RoundedCornerShape(50)) {
            Text("Tentar novamente")
        }
        if (rodape != null) {
            Spacer(Modifier.height(12.dp))
            Text(rodape, color = CinzaTexto, style = MaterialTheme.typography.bodySmall)
        }
    }
}
