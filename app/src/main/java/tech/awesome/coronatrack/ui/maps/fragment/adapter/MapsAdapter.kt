package tech.awesome.coronatrack.ui.maps.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.awesome.coronatrack.databinding.ItemMapsContentBinding
import tech.awesome.data.network.Confirmed
import tech.awesome.data.network.Countries
import tech.awesome.utils.StatusConstant
import tech.awesome.utils.extension.isContains

class MapsAdapter(private val listener: MapsListener, private var status: Int) :
    RecyclerView.Adapter<MapsAdapter.MapsViewHolder>() {
    private var mData = emptyArray<Confirmed>()
    private var mFilteredData = emptyArray<Confirmed>()
    private var countries: Countries? = null
    private var selectedIndex = -1

    interface MapsListener {
        fun onClickItem(confirmed: Confirmed, index: Int)
    }

    inner class MapsViewHolder(val binding: ItemMapsContentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapsViewHolder {
        return MapsViewHolder(
            ItemMapsContentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mFilteredData.size

    override fun onBindViewHolder(holder: MapsViewHolder, position: Int) {
        holder.binding.clMaps.setOnClickListener { listener.onClickItem(mFilteredData[position], position) }
        holder.binding.confirmed = mFilteredData[position]
        val value =
            when (status) {
                StatusConstant.CONFIRMED -> mFilteredData[position].confirmed ?: 0
                StatusConstant.RECOVERED -> mFilteredData[position].recovered ?: 0
                else -> mFilteredData[position].deaths ?: 0
            }

        holder.binding.tvContentValue.value = value
        holder.binding.tvContentValue.status = this.status
        if (countries != null) {
            mFilteredData[position].countryRegion?.let {
                val filteredCountry =
                    countries?.countries?.filter { item -> item.name.isContains(it) }
                if (!filteredCountry.isNullOrEmpty())
                    holder.binding.country = filteredCountry[0]
            }
        }
    }

    fun setData(data: Array<Confirmed>) {
        mData =
            data.distinctBy { if (it.provinceState.isNullOrBlank()) it.countryRegion else it.provinceState }
                .sortedBy { if (it.provinceState.isNullOrBlank()) it.countryRegion else it.provinceState }
                .toTypedArray()
        mFilteredData = mData.copyOf()
        notifyDataSetChanged()
    }

    fun setCountries(country: Countries) {
        countries = country
        notifyDataSetChanged()
    }

    fun setStatus(status: Int) {
        this.status = status
        notifyDataSetChanged()
    }

    fun getStatus() = status

    fun setSelected(index: Int) {
        val prevIndex = selectedIndex
        selectedIndex = index

        if (prevIndex != -1) notifyItemChanged(prevIndex)
        notifyItemChanged(selectedIndex)
    }

    fun setShownData(keyword: String?) {
        mFilteredData = if (keyword.isNullOrBlank()) {
            mData.copyOf()
        } else {
            mData.filter { data ->
                data.provinceState.isContains(keyword) || data.countryRegion.isContains(keyword)
            }.toTypedArray()
        }
        notifyDataSetChanged()
    }
}