package tech.awesome.coronatrack.ui.add_country.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.awesome.coronatrack.databinding.ItemRegionBinding
import tech.awesome.coronatrack.databinding.ItemRegionSelectedBinding
import tech.awesome.data.network.Confirmed
import tech.awesome.utils.extension.isContains

class AddCountryAdapter(private val listener: AddCountryListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData = emptyArray<Confirmed>()
    private var mFilteredData = emptyArray<Confirmed>()
    private var selectedIndex = -1

    interface AddCountryListener {
        fun onClickItem(confirmed: Confirmed, index: Int)
    }

    inner class AddCountryViewHolder(val binding: ItemRegionBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class AddCountrySelectedViewHolder(val binding: ItemRegionSelectedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        if (position == selectedIndex) return 1
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1)
            return AddCountrySelectedViewHolder(
                ItemRegionSelectedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        return AddCountryViewHolder(
            ItemRegionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mFilteredData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddCountryViewHolder -> {
                mFilteredData[position].apply {
                    holder.binding.clCountry.setOnClickListener {
                        listener.onClickItem(
                            this,
                            position
                        )
                    }
                    holder.binding.region = provinceState ?: countryRegion
                    holder.binding.country = countryRegion
                }
            }
            is AddCountrySelectedViewHolder -> {
                mFilteredData[position].apply {
                    holder.binding.clCountrySelected.setOnClickListener {
                        listener.onClickItem(
                            this,
                            position
                        )
                    }
                    holder.binding.region = provinceState ?: countryRegion
                    holder.binding.country = countryRegion
                }
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