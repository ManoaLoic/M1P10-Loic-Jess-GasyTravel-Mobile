package com.example.gasytravel

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import jp.wasabeef.richeditor.RichEditor


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RichText.newInstance] factory method to
 * create an instance of this fragment.
 */
class RichText : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mEditor: RichEditor? = null
    private var mPreview: TextView? = null
    private var value : String? = null

    fun getCurrentValue(): String? {
        return value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rich_text, container, false)

        mEditor = view.findViewById(R.id.editor)
        mEditor?.setEditorHeight(200)
        mEditor?.setEditorFontSize(22)
        mEditor?.setEditorFontColor(Color.BLACK)
        //mEditor?.setEditorBackgroundColor(Color.BLUE)
        //mEditor?.setBackgroundColor(Color.BLUE)
        //mEditor?.setBackgroundResource(R.drawable.bg)
        mEditor?.setPadding(10, 10, 10, 10)
        //mEditor?.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg")
        mEditor?.setPlaceholder("Description")
        //mEditor?.setInputEnabled(false)

        mPreview = view.findViewById(R.id.preview)
        mEditor?.setOnTextChangeListener { text ->
            mPreview?.text = text
            value = text
        }

        view.findViewById<View>(R.id.action_undo).setOnClickListener {
            mEditor?.undo()
        }

        view.findViewById<View>(R.id.action_redo).setOnClickListener {
            mEditor?.redo()
        }

        view.findViewById<View>(R.id.action_bold).setOnClickListener(View.OnClickListener { mEditor!!.setBold() })

        view.findViewById<View>(R.id.action_italic).setOnClickListener(View.OnClickListener { mEditor!!.setItalic() })

        view.findViewById<View>(R.id.action_subscript).setOnClickListener(View.OnClickListener { mEditor!!.setSubscript() })

        view.findViewById<View>(R.id.action_superscript).setOnClickListener(View.OnClickListener { mEditor!!.setSuperscript() })

        view.findViewById<View>(R.id.action_strikethrough).setOnClickListener(View.OnClickListener { mEditor!!.setStrikeThrough() })

        view.findViewById<View>(R.id.action_underline).setOnClickListener(View.OnClickListener { mEditor!!.setUnderline() })

        view.findViewById<View>(R.id.action_heading1).setOnClickListener(View.OnClickListener {
            mEditor!!.setHeading(
                1
            )
        })

        view.findViewById<View>(R.id.action_heading2).setOnClickListener(View.OnClickListener {
            mEditor!!.setHeading(
                2
            )
        })

        view.findViewById<View>(R.id.action_heading3).setOnClickListener(View.OnClickListener {
            mEditor!!.setHeading(
                3
            )
        })

        view.findViewById<View>(R.id.action_heading4).setOnClickListener(View.OnClickListener {
            mEditor!!.setHeading(
                4
            )
        })

        view.findViewById<View>(R.id.action_heading5).setOnClickListener(View.OnClickListener {
            mEditor!!.setHeading(
                5
            )
        })

        view.findViewById<View>(R.id.action_heading6).setOnClickListener(View.OnClickListener {
            mEditor!!.setHeading(
                6
            )
        })

        view.findViewById<View>(R.id.action_txt_color).setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                mEditor!!.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })

        view.findViewById<View>(R.id.action_bg_color).setOnClickListener(object : View.OnClickListener {
            private var isChanged = false
            override fun onClick(v: View) {
                mEditor!!.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
                isChanged = !isChanged
            }
        })

        view.findViewById<View>(R.id.action_indent).setOnClickListener(View.OnClickListener { mEditor!!.setIndent() })

        view.findViewById<View>(R.id.action_outdent).setOnClickListener(View.OnClickListener { mEditor!!.setOutdent() })

        view.findViewById<View>(R.id.action_align_left).setOnClickListener(View.OnClickListener { mEditor!!.setAlignLeft() })

        view.findViewById<View>(R.id.action_align_center).setOnClickListener(View.OnClickListener { mEditor!!.setAlignCenter() })

        view.findViewById<View>(R.id.action_align_right).setOnClickListener(View.OnClickListener { mEditor!!.setAlignRight() })

        view.findViewById<View>(R.id.action_blockquote).setOnClickListener(View.OnClickListener { mEditor!!.setBlockquote() })

        view.findViewById<View>(R.id.action_insert_bullets).setOnClickListener(View.OnClickListener { mEditor!!.setBullets() })

        view.findViewById<View>(R.id.action_insert_numbers).setOnClickListener(View.OnClickListener { mEditor!!.setNumbers() })

        view.findViewById<View>(R.id.action_insert_image).setOnClickListener(View.OnClickListener {
            mEditor!!.insertImage(
                "https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
                "dachshund", 320
            )
        })

        view.findViewById<View>(R.id.action_insert_youtube).setOnClickListener(View.OnClickListener {
            mEditor!!.insertYoutubeVideo(
                "https://www.youtube.com/embed/pS5peqApgUA"
            )
        })

        view.findViewById<View>(R.id.action_insert_audio).setOnClickListener(View.OnClickListener {
            mEditor!!.insertAudio(
                "https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3"
            )
        })

        view.findViewById<View>(R.id.action_insert_video).setOnClickListener(View.OnClickListener {
            mEditor!!.insertVideo(
                "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_10MB.mp4",
                360
            )
        })

        view.findViewById<View>(R.id.action_insert_link).setOnClickListener(View.OnClickListener {
            mEditor!!.insertLink(
                "https://github.com/wasabeef",
                "wasabeef"
            )
        })
        view.findViewById<View>(R.id.action_insert_checkbox).setOnClickListener(View.OnClickListener { mEditor!!.insertTodo() })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RichText.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RichText().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}