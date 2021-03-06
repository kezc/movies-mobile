package pl.org.akai.movies.fragments


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search_movie.*
import pl.org.akai.movies.R
import pl.org.akai.movies.data.SearchRespone
import pl.org.akai.movies.list.MovieAdapter
import pl.org.akai.movies.list.TopSpacingItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchMovieFragment : BaseFragment() {

    private lateinit var movieAdapter: MovieAdapter

    override val layoutId: Int
        get() = R.layout.fragment_search_movie

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieAdapter = MovieAdapter(arrayListOf()) {
            findNavController().navigate(SearchMovieFragmentDirections.toMovieDetails(it.imdbId))
        }

        getMovies("abc")
        setupToolbar()
        initRecyclerView()

    }

    private fun setupToolbar() {
        toolbar.inflateMenu(R.menu.search_menu)
        val searchItem: MenuItem = toolbar.menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                getMovies(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                getMovies(newText)
                return false
            }
        })
        searchView.queryHint = getText(R.string.search_view_text)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }



    private fun getMovies(query: String) {
        if (query.length >= 3) { // poniżej 3 jest za dużo wyników
            service.getSerchedMovies("6ade0e7b", query).enqueue(object : Callback<SearchRespone> {
                override fun onFailure(call: Call<SearchRespone>, t: Throwable) {}

                override fun onResponse(
                    call: Call<SearchRespone>,
                    response: Response<SearchRespone>
                ) {
                    when (response.code()) {
                        200 -> {
                            val searchResponse = response.body()!!
                            if (searchResponse.response) {
                                Log.d("results", response.body()!!.search!!.size.toString())
                                movieAdapter.submitList(response.body()!!.search!!)
                                infoTextView.isVisible = false
                            } else {
                                movieAdapter.submitList(listOf())
                                infoTextView.isVisible = true
                            }
                        }
                        else -> {
                            Log.d("MyLog", "Call: ${call.request().url()}")
                            Log.d("MyLog", "${call.request().headers()}")
                        }
                    }
                }

            })
        }
    }

    private fun initRecyclerView() {
        moviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter = movieAdapter
        }


    }

}
