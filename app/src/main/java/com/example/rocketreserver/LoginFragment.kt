package com.example.rocketreserver

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rocketreserver.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitProgressBar.visibility = View.VISIBLE
        binding.submit.visibility = View.GONE
        lifecycleScope.launchWhenResumed {
            val response = try {
                val email = "email"
                apolloClient.mutation(LoginMutation(email = email)).execute()
            } catch (e: Exception) {
                null
            }

            val token = response?.data?.login?.token
            if (token == null || response.hasErrors()) {
                binding.submitProgressBar.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE
                return@launchWhenResumed
            }

            binding.submitProgressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.GONE
            lifecycleScope.launchWhenResumed {
                val response = try {
                    val email = "email"
                    apolloClient(requireContext()).mutation(LoginMutation(email)).execute()
                } catch (e: Exception) {
                    null
                }

                val token = response?.data?.login?.token
                if (token == null || response.hasErrors()) {
                    binding.submitProgressBar.visibility = View.GONE
                    binding.submit.visibility = View.VISIBLE
                    return@launchWhenResumed
                }

                User.setToken(requireContext(), token)
                findNavController().popBackStack()
            }
        }
    }
}
