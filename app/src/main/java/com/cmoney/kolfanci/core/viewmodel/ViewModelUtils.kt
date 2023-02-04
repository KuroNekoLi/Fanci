package com.cmoney.kolfanci.core.viewmodel

//@Composable
//inline fun <reified VM : ViewModel> viewModel(
//    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
//        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
//    },
//    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current
//): VM {
//    return androidx.lifecycle.viewmodel.compose.viewModel(
//        viewModelStoreOwner = viewModelStoreOwner,
//        factory = ViewModelFactory(
//            owner = savedStateRegistryOwner,
//            defaultArgs = (savedStateRegistryOwner as? NavBackStackEntry)?.arguments,
//            dependencyContainer = LocalDependencyContainer.current,
//        )
//    )
//}
//
//@Composable
//inline fun <reified VM : ViewModel> activityViewModel(): VM {
//    val activity = LocalDependencyContainer.current.activity
//
//    return androidx.lifecycle.viewmodel.compose.viewModel(
//        VM::class.java,
//        activity,
//        null,
//        factory = ViewModelFactory(
//            owner = activity,
//            defaultArgs = null,
//            dependencyContainer = LocalDependencyContainer.current,
//        )
//    )
//}
//
//class ViewModelFactory(
//    owner: SavedStateRegistryOwner,
//    defaultArgs: Bundle?,
//    private val dependencyContainer: DependencyContainer
//) : AbstractSavedStateViewModelFactory(
//    owner,
//    defaultArgs
//) {
//
//    override fun <T : ViewModel> create(
//        key: String,
//        modelClass: Class<T>,
//        handle: SavedStateHandle
//    ): T {
//        return dependencyContainer.createViewModel(modelClass, handle)
//    }
//}