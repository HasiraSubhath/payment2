package com.kotlin.mad.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.mad.adapters.InquiryAdapter
import com.kotlin.mad.models.InquiryModel
import com.kotlin.mad.R
import com.google.firebase.database.*

class InquiryFetchingActivity : AppCompatActivity() {

    private lateinit var empRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var billList: ArrayList<InquiryModel>
    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetching)

        empRecyclerView = findViewById(R.id.rvEmp)
        empRecyclerView.layoutManager = LinearLayoutManager(this)
        empRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        billList = arrayListOf<InquiryModel>()

        getDeliveryData()


    }

    private fun getDeliveryData() {

        empRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("DeliveryDB")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               billList.clear()
                if (snapshot.exists()){
                    for (empSnap in snapshot.children){
                        val billData = empSnap.getValue(InquiryModel::class.java)
                        billList.add(billData!!)
                    }
                    val mAdapter = InquiryAdapter(billList)
                    empRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : InquiryAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@InquiryFetchingActivity, InquiryDetailsActivity::class.java)

                            //put extra(passing data to another activity)
                            intent.putExtra("cId", billList[position].cId)
                            intent.putExtra("cName", billList[position].cName)
                            intent.putExtra("cNumber", billList[position].cNumber)
                            intent.putExtra("cAddress", billList[position].cAddress)
                            intent.putExtra("cEmail", billList[position].cEmail)
                            startActivity(intent)
                        }

                    })

                    empRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}