@file:Suppress("unused")

package com.sdy.luxurytravelapplication.viewbinding

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayout
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/**
 * @author Dylan Cai
 */

fun <VB : ViewBinding> Activity.binding(inflate: (LayoutInflater) -> VB) = lazy {
  inflate(layoutInflater).apply { setContentView(root) }
}

fun <VB : ViewBinding> Fragment.binding(bind: (View) -> VB) =
  FragmentBindingDelegate(bind)

fun <VB : ViewBinding> Dialog.binding(inflate: (LayoutInflater) -> VB) = lazy {
  inflate(layoutInflater).apply { setContentView(root) }
}

fun <VB : ViewBinding> ViewGroup.binding(
  inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
  attachToParent: Boolean = true
) =
  inflate(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)

fun <VB : ViewBinding> TabLayout.Tab.setCustomView(
  inflate: (LayoutInflater) -> VB,
  onBindView: VB.() -> Unit
) {
  customView = inflate(LayoutInflater.from(parent!!.context)).apply(onBindView).root
}

class FragmentBindingDelegate<VB : ViewBinding>(
  private val bind: (View) -> VB
) : ReadOnlyProperty<Fragment, VB> {

  private var isInitialized = false
  private var _binding: VB? = null
  private val binding: VB get() = _binding!!

  @Suppress("UNCHECKED_CAST")
  override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
    if (!isInitialized) {
      thisRef.viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroyView() {
          _binding = null
        }
      })
      _binding = bind(thisRef.requireView())
      isInitialized = true
    }
    return binding
  }
}