/*
 * Copyright (C) 2007-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.text.view.style;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.core.fonts.FontEntry;
import org.geometerplus.zlibrary.core.util.ZLBoolean3;
import org.geometerplus.zlibrary.text.model.*;
import org.geometerplus.zlibrary.text.view.ZLTextStyle;

public class ZLTextExplicitlyDecoratedStyle extends ZLTextDecoratedStyle implements ZLTextStyleEntry.Feature, ZLTextStyleEntry.FontModifier {
	private final ZLTextStyleEntry myEntry;

	public ZLTextExplicitlyDecoratedStyle(ZLTextStyle parent, ZLTextStyleEntry entry) {
		super(parent, parent.Hyperlink);
		myEntry = entry;
	}

	@Override
	protected List<FontEntry> getFontEntriesInternal() {
		final List<FontEntry> parentEntries = Parent.getFontEntries();
		if (myEntry instanceof ZLTextCSSStyleEntry && !BaseStyle.UseCSSFontFamilyOption.getValue()) {
			return parentEntries;
		}

		if (!myEntry.isFeatureSupported(FONT_FAMILY)) {
			return parentEntries;
		}

		final List<FontEntry> entries = myEntry.getFontEntries();
		final int lSize = entries.size();
		if (lSize == 0) {
			return parentEntries;
		}

		final int pSize = parentEntries.size();
		if (pSize > lSize && entries.equals(parentEntries.subList(0, lSize))) {
			return parentEntries;
		}

		final List<FontEntry> allEntries = new ArrayList<FontEntry>(pSize + lSize);
		allEntries.addAll(entries);
		allEntries.addAll(parentEntries);
		return allEntries;
	}

	@Override
	protected int getFontSizeInternal(ZLTextMetrics metrics) {
		if (myEntry instanceof ZLTextCSSStyleEntry && !BaseStyle.UseCSSFontSizeOption.getValue()) {
			return Parent.getFontSize(metrics);
		}

		// Yes, Parent.Parent, not Parent (parent = current tag pre-defined size,
		// we want to override it)
		// TODO: use _previous_tag_value_
		final int baseFontSize = Parent.Parent.getFontSize(metrics);
		if (myEntry.isFeatureSupported(FONT_STYLE_MODIFIER)) {
			if (myEntry.getFontModifier(FONT_MODIFIER_INHERIT) == ZLBoolean3.B3_TRUE) {
				return baseFontSize;
			}
			if (myEntry.getFontModifier(FONT_MODIFIER_LARGER) == ZLBoolean3.B3_TRUE) {
				return baseFontSize * 120 / 100;
			}
			if (myEntry.getFontModifier(FONT_MODIFIER_SMALLER) == ZLBoolean3.B3_TRUE) {
				return baseFontSize * 100 / 120;
			}
		}
		if (myEntry.isFeatureSupported(LENGTH_FONT_SIZE)) {
			return myEntry.getLength(LENGTH_FONT_SIZE, metrics, baseFontSize);
		}
		return Parent.getFontSize(metrics);
	}

	@Override
	protected boolean isBoldInternal() {
		switch (myEntry.getFontModifier(FONT_MODIFIER_BOLD)) {
			case B3_TRUE:
				return true;
			case B3_FALSE:
				return false;
			default:
				return Parent.isBold();
		}
	}
	@Override
	protected boolean isItalicInternal() {
		switch (myEntry.getFontModifier(FONT_MODIFIER_ITALIC)) {
			case B3_TRUE:
				return true;
			case B3_FALSE:
				return false;
			default:
				return Parent.isItalic();
		}
	}
	@Override
	protected boolean isUnderlineInternal() {
		switch (myEntry.getFontModifier(FONT_MODIFIER_UNDERLINED)) {
			case B3_TRUE:
				return true;
			case B3_FALSE:
				return false;
			default:
				return Parent.isUnderline();
		}
	}
	@Override
	protected boolean isStrikeThroughInternal() {
		switch (myEntry.getFontModifier(FONT_MODIFIER_STRIKEDTHROUGH)) {
			case B3_TRUE:
				return true;
			case B3_FALSE:
				return false;
			default:
				return Parent.isStrikeThrough();
		}
	}

	@Override
	public int getLeftIndentInternal(ZLTextMetrics metrics, int fontSize) {
		if (myEntry instanceof ZLTextCSSStyleEntry && !BaseStyle.UseCSSMarginsOption.getValue()) {
			return Parent.getLeftIndent(metrics);
		}

		if (!myEntry.isFeatureSupported(LENGTH_LEFT_INDENT)) {
			return Parent.getLeftIndent(metrics);
		}
		return myEntry.getLength(LENGTH_LEFT_INDENT, metrics, fontSize);
	}
	@Override
	public int getRightIndentInternal(ZLTextMetrics metrics, int fontSize) {
		if (myEntry instanceof ZLTextCSSStyleEntry && !BaseStyle.UseCSSMarginsOption.getValue()) {
			return Parent.getRightIndent(metrics);
		}

		if (!myEntry.isFeatureSupported(LENGTH_RIGHT_INDENT)) {
			return Parent.getRightIndent(metrics);
		}
		return myEntry.getLength(LENGTH_RIGHT_INDENT, metrics, fontSize);
	}
	@Override
	protected int getFirstLineIndentInternal(ZLTextMetrics metrics, int fontSize) {
		if (myEntry instanceof ZLTextCSSStyleEntry && !BaseStyle.UseCSSMarginsOption.getValue()) {
			return Parent.getFirstLineIndent(metrics);
		}

		if (!myEntry.isFeatureSupported(LENGTH_FIRST_LINE_INDENT)) {
			return Parent.getFirstLineIndent(metrics);
		}
		return myEntry.getLength(LENGTH_FIRST_LINE_INDENT, metrics, fontSize);
	}
	@Override
	protected int getLineSpacePercentInternal() {
		// TODO: implement
		return Parent.getLineSpacePercent();
	}
	@Override
	protected int getVerticalAlignInternal(ZLTextMetrics metrics, int fontSize) {
		// TODO: implement
		if (myEntry.isFeatureSupported(LENGTH_VERTICAL_ALIGN)) {
			return myEntry.getLength(LENGTH_VERTICAL_ALIGN, metrics, fontSize);
		} else if (myEntry.isFeatureSupported(NON_LENGTH_VERTICAL_ALIGN)) {
			switch (myEntry.getVerticalAlignCode()) {
				default:
					return Parent.getVerticalAlign(metrics);
				case 0: // sub
					return ZLTextStyleEntry.compute(
						new ZLTextStyleEntry.Length((short)-50, ZLTextStyleEntry.SizeUnit.EM_100),
						metrics, fontSize, LENGTH_VERTICAL_ALIGN
					);
				case 1: // super
					return ZLTextStyleEntry.compute(
						new ZLTextStyleEntry.Length((short)50, ZLTextStyleEntry.SizeUnit.EM_100),
						metrics, fontSize, LENGTH_VERTICAL_ALIGN
					);
				/*
				case 2: // top
					return 0;
				case 3: // text-top
					return 0;
				case 4: // middle
					return 0;
				case 5: // bottom
					return 0;
				case 6: // text-bottom
					return 0;
				case 7: // initial
					return 0;
				case 8: // inherit
					return 0;
				*/
			}
		} else {
			return Parent.getVerticalAlign(metrics);
		}
	}
	@Override
	protected int getSpaceBeforeInternal(ZLTextMetrics metrics, int fontSize) {
		if (myEntry instanceof ZLTextCSSStyleEntry && !BaseStyle.UseCSSMarginsOption.getValue()) {
			return Parent.getSpaceBefore(metrics);
		}

		if (!myEntry.isFeatureSupported(LENGTH_SPACE_BEFORE)) {
			return Parent.getSpaceBefore(metrics);
		}
		return myEntry.getLength(LENGTH_SPACE_BEFORE, metrics, fontSize);
	}
	@Override
	protected int getSpaceAfterInternal(ZLTextMetrics metrics, int fontSize) {
		if (myEntry instanceof ZLTextCSSStyleEntry && !BaseStyle.UseCSSMarginsOption.getValue()) {
			return Parent.getSpaceAfter(metrics);
		}

		if (!myEntry.isFeatureSupported(LENGTH_SPACE_AFTER)) {
			return Parent.getSpaceAfter(metrics);
		}
		return myEntry.getLength(LENGTH_SPACE_AFTER, metrics, fontSize);
	}
	public byte getAlignment() {
		if (myEntry instanceof ZLTextCSSStyleEntry && !BaseStyle.UseCSSTextAlignmentOption.getValue()) {
			return Parent.getAlignment();
		}
		return
			myEntry.isFeatureSupported(ALIGNMENT_TYPE)
				? myEntry.getAlignmentType()
				: Parent.getAlignment();
	}

	public boolean allowHyphenations() {
		// TODO: implement
		return Parent.allowHyphenations();
	}
}
