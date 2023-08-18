package com.example.sunlibrary.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sunlibrary.ui.theme.SunLibraryTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.Serializable

private const val ACCOUNT_LIST_FILE = "AccountList.txt"
private const val TAP_REGISTER = 0
private const val TAP_LOGIN = 1

private var mDirectoryPath: String = ""
private var mAccountList: MutableList<LoginInfo> = mutableListOf()

@Composable
fun LoginActivity(navController: NavController, directoryPath: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Spacer(modifier = Modifier.weight(1.0f))
        AppName()
        SetTab(navController, directoryPath)
        Spacer(modifier = Modifier.weight(2.0f))
    }
}

@Composable
fun AppName() {
    Text(
        text = "SUN図書館",
        style = MaterialTheme.typography.headlineLarge,
        fontSize = 50.sp
    )
}

@Composable
fun SetTab(navController: NavController, directoryPath: String) {
    val accountName = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val confirmPassword = rememberSaveable { mutableStateOf("") }
    val userName = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val tabTitles = listOf("新規登録", "ログイン")

    // アカウントリストを取得しておく
    mDirectoryPath = directoryPath
    val file = File(mDirectoryPath + ACCOUNT_LIST_FILE)
    if (file.exists()) {
        mAccountList = JSonFileFactory().readAccountInfoFile(mDirectoryPath)
    }

    val selectedTabIndex = remember { mutableStateOf(TAP_REGISTER) }
    TabRow(
        selectedTabIndex.value,
        modifier = Modifier
            .fillMaxWidth()
            .requiredWidth(200.dp)
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex.value == index,
                onClick = { selectedTabIndex.value = index }
            ) {
                Text(text = title)
            }
        }
    }
    when (selectedTabIndex.value) {
        // 新規登録タブ
        TAP_REGISTER -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NameBox(accountName, "メールアドレス")
                PasswordBox(password, "パスワード")
                PasswordBox(confirmPassword, "パスワード(確認用)")
                NameBox(userName, "ユーザ名")

                Button(
                    content = {
                        Text(
                            text = "登録",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    onClick = {
                        onClickRegisterBtn(
                            navController,
                            context,
                            accountName,
                            password,
                            confirmPassword,
                            userName
                        )
                    })
            }
        }

        // ログインタブ
        TAP_LOGIN -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NameBox(accountName, "メールアドレス")
                PasswordBox(password, "パスワード")
                Text(
                    text = "パスワード忘れましたか？",
                    modifier = Modifier.align(Alignment.End)

                )
                Button(
                    content = {
                        Text(
                            text = "ログイン",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    onClick = {
                        onClickLoginBtn(navController, context, accountName, password)
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameBox(name: MutableState<String>, hintText: String) {
    OutlinedTextField(
        modifier = Modifier.width(450.dp),
        value = name.value,
        onValueChange = { name.value = it },
        label = { Text(hintText) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordBox(password: MutableState<String>, hintText: String) {
    val show = remember { mutableStateOf(false) }
    val icon = if (show.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
    val desc = if (show.value) "Hide password" else "Show password"
    val transformation =
        if (show.value) VisualTransformation.None else PasswordVisualTransformation()
    OutlinedTextField(
        modifier = Modifier.width(450.dp),
        value = password.value,
        onValueChange = { password.value = it },
        visualTransformation = transformation,
        label = { Text(hintText) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { show.value = !show.value }) {
                Icon(imageVector = icon, contentDescription = desc)
            }
        }
    )
}

private fun onClickRegisterBtn(
    navController: NavController,
    context: Context,
    accountName: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>,
    userName: MutableState<String>
) {
    val toast = Toast.makeText(
        context,
        "",
        Toast.LENGTH_SHORT
    )

    // 入力された情報をチェック
    var msg = ""
    if (accountName.value.isEmpty()) {
        msg = "メールアドレスを入力してください。"
    } else if (password.value.isEmpty()) {
        msg = "パスワードを入力してください。"
    } else if (confirmPassword.value.isEmpty()) {
        msg = "パスワード（確認用）を入力してください。"
    } else if (userName.value.isEmpty()) {
        msg = "ユーザ名を入力してください。"
    }
    if (msg.isNotEmpty()) {
        toast.setText(msg)
        toast.show()
        return
    }

    // 既に登録される場合
    for (account in mAccountList) {
        if (account.accountName == accountName.value) {
            msg = accountName.value + "は既に登録されています。別のメールアドレスをお試しください。"
        } else if (account.userName == userName.value) {
            msg = userName.value + "は既に登録されています。別のユーザ名をお試しください。"
        }
    }
    if (msg.isNotEmpty()) {
        toast.setText(msg)
        toast.show()
        return
    }

    // 入力されたパスワードをチェック
    if (password.value != confirmPassword.value) {
        msg = "パスワードと確認用パスワードが一致しません。再度入力してください。"
        toast.setText(msg)
        toast.show()
        return
    }

    // エラーが発生されない場合、アカウントリストファイルに情報を保存する。
    val data: MutableList<LoginInfo> = mutableListOf()
    if (mAccountList.isNotEmpty()) {
        for (account in mAccountList) {
            data.add(account)
        }
    }

    data.add(
        LoginInfo(
            accountName.value,
            password.value,
            userName.value
        )
    )
    if (JSonFileFactory().saveAccountInfoFile(data, mDirectoryPath)) {
        accountName.value = ""
        password.value = ""
        navController.navigate("BorrowActivity")
    } else {
        msg = "登録失敗しました。"
        toast.setText(msg)
        toast.show()
    }
}

private fun onClickLoginBtn(
    navController: NavController,
    context: Context,
    accountName: MutableState<String>,
    password: MutableState<String>
) {
    if (mAccountList.isNotEmpty()) {
        for (account in mAccountList) {
            // ログイン成功の場合
            if (account.accountName == accountName.value && account.password == password.value) {
                accountName.value = ""
                password.value = ""
                navController.navigate("BorrowActivity")
                return
            }
        }

        // ログイン失敗の場合
        val toast = Toast.makeText(
            context,
            "ログイン失敗しました。メールアドレスとパスワードを再度確認してください。",
            Toast.LENGTH_SHORT
        )
        toast.show()
    } else {
        val toast = Toast.makeText(
            context,
            "アカウントを登録してください。",
            Toast.LENGTH_SHORT
        )
        toast.show()
    }
}

class LoginInfo : Serializable {
    var accountName: String = ""
    var password: String = ""
    var userName: String = ""

    constructor(accountName: String, password: String, userName: String) {
        this.accountName = accountName
        this.password = password
        this.userName = userName
    }
}

class JSonFileFactory {
    fun saveAccountInfoFile(data: MutableList<LoginInfo>, path: String): Boolean {
        try {
            val gson = Gson()
            val file = FileWriter(path + ACCOUNT_LIST_FILE)
            gson.toJson(data, file)
            file.close()
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }

    fun readAccountInfoFile(path: String): MutableList<LoginInfo> {
        var data: MutableList<LoginInfo> = mutableListOf()
        try {
            val gson = Gson()
            val file = FileReader(path + ACCOUNT_LIST_FILE)
            data = gson.fromJson(
                file,
                object : TypeToken<MutableList<LoginInfo>>() {
                }.type
            )
            file.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return data
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val navController = rememberNavController()
    SunLibraryTheme {
        LoginActivity(navController, "")
    }
}