package tech.awesome.coronatrack.ui.daily_updates.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.awesome.data.network.Countries
import tech.awesome.data.network.Daily
import tech.awesome.coronatrack.databinding.ItemDailyContentBinding
import tech.awesome.coronatrack.databinding.ItemDailyLoadingBinding
import tech.awesome.utils.StatusConstant
import tech.awesome.utils.extension.isContains

class DailyUpdatesAdapter(private var status: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dailys = emptyArray<Daily>()
    private var countries: Countries? = null

    private var page: Int = 1
    private var numPerPage: Int = 10

    var isLastPage = true
    private var shownPage =
        if (dailys.size > (page * numPerPage)) (page * numPerPage) + 1
        else dailys.size

    override fun getItemViewType(position: Int): Int {
        if (!isLastPage && position == shownPage - 1) return 1
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == 1) DailyUpdatesLoadingViewHolder(
            ItemDailyLoadingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) else
            DailyUpdatesViewHolder(
                ItemDailyContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

    override fun getItemCount(): Int = shownPage

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DailyUpdatesViewHolder -> {
                holder.binding.daily = dailys[position]
                val value =
                    when (status) {
                        StatusConstant.CONFIRMED -> dailys[position].confirmed
                        StatusConstant.RECOVERED -> dailys[position].recovered
                        else -> dailys[position].deaths
                    }

                holder.binding.tvContentValue.value = value
                holder.binding.tvContentValue.status = this.status
                if (countries != null) {
                    dailys[position].countryRegion?.let {
                        val filteredCountry =
                            countries?.countries?.filter { item -> item.name.isContains(it) }
                        if (!filteredCountry.isNullOrEmpty())
                            holder.binding.country = filteredCountry[0]
                    }
                }
            }
        }
    }

    fun setDailys(newDailys: Array<Daily>) {
        dailys = newDailys
        updateShownPage()
        notifyDataSetChanged()
    }

    fun setCountries(country: Countries) {
        countries = country
        resetShownPage()
        notifyDataSetChanged()
    }

    fun setStatus(status: Int) {
        this.status = status
        resetShownPage()
        notifyDataSetChanged()
    }

    fun getStatus() = status

    private fun resetShownPage() {
        page = 1
        updateShownPage()
    }

    fun loadMore() {
        val currentLastIndex = shownPage - 1
        page += 1
        updateShownPage()
        notifyItemChanged(currentLastIndex)
        notifyItemRangeInserted(
            currentLastIndex,
            (shownPage).coerceAtMost(dailys.size) - 1
        )
    }

    private fun updateShownPage() {
        if (dailys.size > (page * numPerPage)) {
            isLastPage = false
            shownPage = (page * numPerPage) + 1
        } else {
            isLastPage = true
            shownPage = dailys.size
        }
    }

    inner class DailyUpdatesViewHolder(val binding: ItemDailyContentBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DailyUpdatesLoadingViewHolder(binding: ItemDailyLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)
}