package kz.protectorai.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import kz.protectorai.CommonHardcode
import kz.protectorai.core.Eventful
import kz.protectorai.navigation.auth.AuthComponent
import kz.protectorai.navigation.feed.FeedComponent

interface RootComponent : Eventful<RootComponent.Event> {

    val stack: Value<ChildStack<*, Child>>

    fun onBackPressed()
    fun onBackClicked(toIndex: Int)

    class Default(
        componentContext: ComponentContext
    ) : RootComponent, ComponentContext by componentContext {

        private val navigation = StackNavigation<Config>()

        override val stack: Value<ChildStack<*, Child>> = childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = CommonHardcode.wildcard { Config.Feed }
        ) { config, _ ->
            when (config) {
                is Config.Auth -> Child.Auth(AuthComponent(componentContext))
                is Config.Feed -> Child.Feed(FeedComponent(componentContext))
            }
        }

        override fun onBackPressed() = navigation.pop()

        override fun onBackClicked(toIndex: Int) = navigation.popTo(toIndex)

        override fun onEvent(event: Event) {
            when (event) {
                Event.AuthCompleted -> navigation.pushNew(Config.Feed)
            }
        }
    }

    sealed interface Child {
        class Auth(val component: AuthComponent) : Child
        class Feed(val component: FeedComponent) : Child
    }

    @Serializable
    sealed interface Config {
        @Serializable data object Auth : Config
        @Serializable data object Feed : Config
    }

    interface Event : Eventful.Event {
        data object AuthCompleted : Event
    }
}