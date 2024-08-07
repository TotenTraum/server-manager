package ru.ttraum.client

import io.kvision.*
import io.kvision.core.*
import io.kvision.form.text.textInput
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.panel.hPanel
import io.kvision.panel.root
import io.kvision.panel.splitPanel
import io.kvision.panel.vPanel
import io.kvision.state.bindTo
import io.kvision.theme.Theme
import io.kvision.theme.ThemeManager
import io.kvision.toolbar.buttonGroup
import io.kvision.toolbar.toolbar
import io.kvision.utils.perc
import io.kvision.utils.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow

val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

class App : Application(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    init {
        ThemeManager.init(Theme.DARK)
    }

    override fun start(state: Map<String, Any>) {
        val url = MutableStateFlow("http://localhost:8081")
        val fsTree = FileSystemTreeView(url)

        root("kvapp") {
            vPanel {
                hPanel {
                    verticalAlign = VerticalAlign.MIDDLE
                    justifyContent = JustifyContent.CENTER
                    justifySelf = JustifyItems.CENTER
                    alignContent = AlignContent.SPACEBETWEEN
                    alignItems = AlignItems.CENTER
                    width = 100.perc
                    spacing = 5
                    paddingLeft = 10.px
                    paddingRight = 10.px
                    div("server url: ") {
                        fontWeight = FontWeight.BOLD
                        textAlign = TextAlign.CENTER
                    }
                    textInput {
                        placeholder = "server host"
                    }.bindTo(url)
                    button("ping")
                }
                toolbar {
                    paddingLeft = 10.px
                    paddingRight = 10.px
                    paddingTop = 5.px
                    paddingBottom = 5.px
                    borderTop = Border(1.px, BorderStyle.SOLID)
                    borderBottom = Border(1.px, BorderStyle.SOLID)
                    buttonGroup {
                        button("Open Settings") {}
                        button("Close Settings") {}
                    }
                    buttonGroup {
                        button("Open File") {}
                        button("Close File") {}
                    }
                }
                splitPanel {
                    vPanel {
                        fsTree.run {
                            renderMenu()
                        }
                    }
                    div {
                        span("placeholder")
                    }
                }
            }
        }
    }
}

fun main() {
    startApplication(
        ::App,
        module.hot,
        BootstrapModule,
        BootstrapCssModule,
        DatetimeModule,
        RichTextModule,
        TomSelectModule,
        BootstrapUploadModule,
        ImaskModule,
        ToastifyModule,
        FontAwesomeModule,
        PrintModule,
        ChartModule,
        TabulatorModule,
        TabulatorCssBootstrapModule,
        MapsModule,
        CoreModule
    )
}
