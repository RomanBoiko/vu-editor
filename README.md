vu-editor
=========

Pragmatic programming editor. [![Build Status](https://travis-ci.org/RomanBoiko/vu-editor.png)](https://travis-ci.org/RomanBoiko/vu-editor)

Distributed under [The MIT License](http://roman-boiko.mit-license.org).

[Executable Jar](https://github.com/RomanBoiko/vu-editor/blob/master/vue.jar?raw=true) [![endorse](https://api.coderwall.com/romanboiko/endorsecount.png)](https://coderwall.com/romanboiko)


Features:
---------

- [x] - ready
- [ ] - not ready yet

---

- [x] full editor packaged into single shell file(POSIX package)
- [x] full editor in executable jar(Windows package)
- [x] no third-party libs, core java
- [x] show line numbers
- [x] XML formatting
- [x] open with file from command line
- [x] save file
- [x] line up/down
- [x] delete line
- [x] show whitespaces
- [x] join lines
- [x] help system
- [x] ESC to return to edit mode
- [x] license
- [x] log file
- [x] handling(logging) all exceptions/errors
- [x] exit from editor shortcut(not dependant on window manager's abilities)
- [x] to upper/lower case
- [x] file explorer(tree)
- [ ] duplicate line
- [ ] multiline indent
- [ ] multiline unindent
- [ ] open stream from command line (use in pipe)
- [ ] search in file
- [ ] replace in file
- [ ] select number of whole lines
- [ ] open file by name(fuzzy)
- [ ] search in folder
- [ ] showing file name under edit in title on alt+tab
- [ ] show opened file path in status bar
- [ ] copy current file path/name into clipboard
- [ ] search in folder results in separate mode with navigation to editor
- [ ] multiple buffers/documents
- [ ] switch between buffers
- [ ] Matching Bracket highlight
- [ ] Syntax highlight(config)
- [ ] auto-refactoring
- [ ] vertical edit
- [ ] any command menu
- [ ] use spaces instead of tabs
- [ ] tab width configurable
- [ ] line wrapping
- [ ] outlines(list of functions, paragraphs)
- [ ] undo
- [ ] redo
- [ ] highlight words similar to the one under cursor
- [ ] goto occurences of the word under cursor
- [ ] custom font
- [ ] config

Known bugs:
-----------

- [x] fixed
- [ ] not fixed yet

---

- [x] 1. Selected text becomes unvisible when switching whitespace highlight off (reported by Samir Talwar)
- [ ] 2. Few line numbers appear in the same raw - not reproduced yet, maybe due to font size/resolution (reported by Grzegorz Ligas)
- [x] 3. After switching from help back to edit area file is reloaded and because of that not saved changes are lost
- [x] 4. If we are in read-only perspective(i.e. Help), and doing Alt+Tab, and than coming back to Editor - cursor is not visible any more, even if to come back to EditorPerspective(with Esc). Workaround: after switching to EditorPerspective - do Alt+Tab twice 
- [ ] 5. after folder is opened in FileExplorer - cursor jumps to last child of this folder 

