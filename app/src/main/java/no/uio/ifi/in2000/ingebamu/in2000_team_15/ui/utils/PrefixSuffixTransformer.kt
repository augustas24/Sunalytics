package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PrefixSuffixTransformer(val prefix: String = "", val suffix: String = "") :
    VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val result = AnnotatedString(prefix) + text + AnnotatedString(suffix)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) =
                offset + prefix.length
            override fun transformedToOriginal(offset: Int) =
                if(text.length < offset){
                    text.length
                } else if(offset < prefix.length){
                    0
                }else {
                    offset - prefix.length
                }
        }
        return TransformedText(result, offsetMapping)
    }
}