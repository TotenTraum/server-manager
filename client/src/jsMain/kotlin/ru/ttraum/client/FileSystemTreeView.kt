package ru.ttraum.client

import io.kvision.core.*
import io.kvision.form.text.textInput
import io.kvision.html.*
import io.kvision.panel.SimplePanel
import io.kvision.panel.hPanel
import io.kvision.state.bind
import io.kvision.state.bindTo
import io.kvision.toast.Toast
import io.kvision.utils.perc
import io.kvision.utils.px
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.ttraum.shared.api.dto.GetFSResponse

class FileSystemTreeView(private val url: StateFlow<String>) {
    private var treeDiv: Div? = null
    private val pathState = MutableStateFlow("")

    private val fsNode: MutableStateFlow<FSNode.MainNode> = MutableStateFlow(FSNode.MainNode())

    fun SimplePanel.renderMenu() {
        span {
            content = "FileSystem"
            paddingTop = 5.px
            paddingBottom = 5.px
            align = Align.CENTER
            fontSize = 18.px
            fontWeight = FontWeight.BOLD
        }
        hPanel {
            textInput {
                value = "/"
                placeholder = "start directory"
            }.bindTo(pathState)
            button("") {
                icon = "fa-solid fa-magnifying-glass"
                onClick {
                    this@FileSystemTreeView.updateTree(pathState.value)
                }
            }
        }
        treeDiv = div {
            paddingLeft = 5.px
            printDivTree()
        }
    }

    private fun printDivTree(): Div {
        return Div {
            fsNode.asStateFlow().value.nodes.forEach { node ->
                div {
                    leaf(0.px, node)
                }
            }
        }
    }

    private fun updateTree(path: String) {
        val fsSource = FileSystemDataSource(url.value)
        appScope.launch {
            runCatching {
                fsSource.get(path, false)
            }.onFailure {
                Toast.danger(it.message.toString())
            }.onSuccess {
                when (it) {
                    is GetFSResponse.DirectoryEntry -> {
                        createPathToDirectory(path, it)
                        treeDiv?.removeAll()
                        treeDiv?.add(printDivTree())
                    }

                    is GetFSResponse.FileEntry -> {
                        Toast.info("Чел, ты пытаешься открыть файл, но я тебе этого не дам сделать")
                    }
                }
            }
        }
    }

    private fun updateTree(node: FSNode) {
        val list = mutableListOf<String>()
        var iterNode = node
        list.add(iterNode.name)
        while (iterNode.parentNode != null){
            iterNode = iterNode.parentNode!!
            list.add(iterNode.name)
        }
        val path = list.filter{it.isNotBlank() && it != "/"}.reversed().joinToString("/")
        pathState.value = path
        updateTree(path)
    }



    private fun createPathToDirectory(path: String, directoryEntry: GetFSResponse.DirectoryEntry) {
        val pathNodesNames = path.removePrefix("/").split("/").filter { it.isNotBlank() }
        var node: FSNode = fsNode.value
        pathNodesNames.forEach { nodeName ->
            when (node) {
                is FSNode.RootNode -> {
                    node = (node as FSNode.RootNode).nodes.find { it.name == nodeName }
                        ?: run {
                            val dirNode = FSNode.DirectoryEntry(nodeName, node)
                            (node as FSNode.RootNode).nodes.add(dirNode)
                            dirNode
                        }
                }

                else -> Toast.danger("undefined directory name: $nodeName")
            }
        }

        val rootNode = (node as FSNode.RootNode)

        directoryEntry.entities.forEach { entity ->
            if(rootNode.nodes.all { it.name != entity.path }) {
                val createdNode = if (entity.isFile)
                    FSNode.FileNode(entity.path, rootNode)
                else
                    FSNode.DirectoryEntry(entity.path, rootNode)
                rootNode.nodes.add(createdNode)
            }
        }
    }

    private fun Tag.leaf(leftPadding: CssSize, node: FSNode) {
        val state = MutableStateFlow(true)
        div {
            borderTop = Border(1.px, BorderStyle.SOLID)
            div {
                paddingLeft = leftPadding
            }.bind(state) { isButtonClicked ->
                hPanel {
                    paddingTop = 3.px
                    paddingBottom = 3.px
                    spacing = 5
                    verticalAlign = VerticalAlign.MIDDLE
                    justifyContent = JustifyContent.FLEXEND
                    span(node.name){
                        alignContent = AlignContent.START
                        align = Align.LEFT
                        width = 100.perc
                    }
                    button("") {
                        padding = 5.px
                        icon = "fa-solid fa-magnifying-glass"
                        onClick {
                            updateTree(node)
                        }
                    }
                    if (node !is FSNode.FileNode)
                        onClick {
                            state.value = !state.value
                        }
                }
                if (node is FSNode.RootNode && isButtonClicked)
                    node.nodes.forEach { subNode ->
                        leaf(15.px, subNode)
                    }
            }
        }
    }

    sealed class FSNode(val name: String, val parentNode: FSNode?) {
        open class RootNode(name: String, parentNode: FSNode?, val nodes: MutableList<FSNode> = mutableListOf()) : FSNode(name, parentNode) {
            override fun toString(): String {
                return "RootNode(nodes=$nodes)"
            }
        }

        class MainNode : RootNode("/", null) {
            override fun toString(): String {
                return "MainNode(${nodes.joinToString(",")})"
            }
        }

        class DirectoryEntry(name: String, parentNode: FSNode) : RootNode(name, parentNode){
            override fun toString(): String {
                return "DirNode($name, ${nodes.joinToString(",")})"
            }
        }
        class FileNode(name: String, parentNode: FSNode) : FSNode(name, parentNode)
    }
}