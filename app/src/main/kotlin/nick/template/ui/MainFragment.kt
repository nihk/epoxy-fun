package nick.template.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.epoxy.*
import java.util.*
import nick.template.R
import nick.template.databinding.MainFragmentBinding
import javax.inject.Inject
import nick.template.data.Row
import nick.template.data.Thing
import nick.template.databinding.ItemBinding
import nick.template.databinding.ThingBinding

class MainFragment @Inject constructor(
    private val factory: MainViewModel.Factory
) : Fragment(R.layout.main_fragment) {
    private val viewModel: MainViewModel by viewModels { factory.create(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = MainFragmentBinding.bind(view)
        binding.recyclerView.addItemDecoration(MarginItemDecoration())

        val rows = List(100) { Row(UUID.randomUUID().toString()) }
        val things = List(100) { Thing(UUID.randomUUID().toString()) }

        binding.recyclerView.withModels {
            rows.zip(things) { row, thing ->
                addRow(row)

                thing {
                    id(thing.id)
                    thing(thing)
                    clicks(View.OnClickListener {
                        Toast.makeText(view.context, "Clicked a thing", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    }

    private fun ModelCollector.addRow(row: Row) {
        rowThatGetsKotlinified {
            id(row.id)
            rowThisIsTheField(row)
            clicks(View.OnClickListener {
                Toast.makeText(requireContext(), "Clicked a row", Toast.LENGTH_SHORT).show()
            })
        }
    }
}

// This is kind of like an RV adapter; it owns the data (though just 1 element), has a binding
// function, and knows about the item layout.
@EpoxyModelClass
abstract class RowThatGetsKotlinified : EpoxyModelWithHolder<RowThatGetsKotlinified.Holder>() {
    override fun getDefaultLayout() = R.layout.item

    @EpoxyAttribute
    lateinit var rowThisIsTheField: Row

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clicks: View.OnClickListener

    override fun bind(holder: Holder) {
        holder.binding.id.text = rowThisIsTheField.id
        holder.binding.root.setOnClickListener(clicks)
    }

    // This is like an RV ViewHolder
    class Holder : EpoxyHolder() {
        lateinit var binding: ItemBinding

        override fun bindView(itemView: View) {
            binding = ItemBinding.bind(itemView)
        }
    }
}

// Note how the Model name suffix gets removed by the compiler
@EpoxyModelClass
abstract class ThingModel : EpoxyModelWithHolder<ThingModel.Holder>() {
    override fun getDefaultLayout(): Int = R.layout.thing

    @EpoxyAttribute
    lateinit var thing: Thing

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clicks: View.OnClickListener

    override fun bind(holder: Holder) {
        holder.bind(thing, clicks)
    }

    class Holder : EpoxyHolder() {
        private lateinit var binding: ThingBinding

        override fun bindView(itemView: View) {
            binding = ThingBinding.bind(itemView)
        }

        // Optionally have this bind function, or just bind in the ThingModel
        fun bind(thing: Thing, clicks: View.OnClickListener) {
            binding.id.text = thing.id
            binding.root.setOnClickListener(clicks)
        }
    }
}
