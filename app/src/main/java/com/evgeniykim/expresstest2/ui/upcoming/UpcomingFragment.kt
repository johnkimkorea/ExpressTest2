package com.evgeniykim.expresstest2.ui.upcoming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.evgeniykim.expresstest2.R
import com.evgeniykim.expresstest2.api.MovieDbClient
import com.evgeniykim.expresstest2.api.MovieDbInterface
import com.evgeniykim.expresstest2.repository.NetworkState
import com.evgeniykim.expresstest2.ui.popular_movie.PopularMoviePagedListAdapter
import com.evgeniykim.expresstest2.ui.toprated.TopratedViewModel
import kotlinx.android.synthetic.main.fragment_popular.*
import kotlinx.android.synthetic.main.fragment_popular.rv_movie_list
import kotlinx.android.synthetic.main.fragment_top_rated.*
import kotlinx.android.synthetic.main.fragment_upcoming.*


class UpcomingFragment : Fragment() {
    private lateinit var viewModel: UpcomingViewModel
    lateinit var movieRepository: UpcomingPagedListRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_upcoming, container, false)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService: MovieDbInterface = MovieDbClient.getClient()

        movieRepository = UpcomingPagedListRepository(apiService)
        viewModel = getViewModel()

        val movieAdapter = PopularMoviePagedListAdapter(requireContext())
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = movieAdapter.getItemViewType(position)
                if (viewType == movieAdapter.MOVIE_VIEW_TYPE) return 1
                else return 3
            }
        }

        rv_movie_list?.layoutManager = gridLayoutManager
        rv_movie_list?.setHasFixedSize(true)
        rv_movie_list?.adapter = movieAdapter

        viewModel.moviePagedList.observe(viewLifecycleOwner, Observer { movieAdapter.submitList(it) })
        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            progress_bar_upcoming.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error_upcoming.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) movieAdapter.setNetworkState(it)
        })
    }


    private fun getViewModel(): UpcomingViewModel {
        return ViewModelProviders.of(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return UpcomingViewModel(movieRepository) as T
            }
        })[UpcomingViewModel::class.java]
    }
}