package cn.disy920.oks.message

import cn.disy920.oks.message.enums.Faces
import cn.disy920.oks.message.tag.Receivable
import cn.disy920.oks.message.tag.Sendable

/**
 * 表示一个表情消息，仅适用于QQ的基本表情（表情商店的额外大表情与自定义收藏表情不包含在内）
 * @param code 表情的代码，常用的表情代码请参考[Faces]
 * @param name 表情的名称
 * @see Faces
 */
class Face(val code: Int, val name: String?) : Message, Sendable, Receivable {
    override fun contentToString(): String {
        return Faces.getFaceByCode(code)?.let { face ->
            return@let face.name
        } ?: "[表情:$code]"
    }
}