(function () {
    'use strict';

    tinymce.PluginManager.add('coviformat', function(editor, url) {
        var _tool = tinymce.util.Tools.resolve('tinymce.util.Tools');

        editor.ui.registry.addMenuItem('coviindent', {
            text: 'Increase indent',
            icon: 'indent',
            onAction: function() {
                return editor.execCommand('indent');
            }
        });

        editor.ui.registry.addMenuItem('covioutdent', {
            text: 'Decrease indent',
            icon: 'outdent',
            onAction: function() {
                return editor.execCommand('outdent');
            }
        });

        editor.ui.registry.addNestedMenuItem('covinumberlist', {
            text: tinymce.util.I18n.translate("Numbered list"),
            getSubmenuItems: function() {
                return [{
                    type: 'menuitem',
                    text: 'Default',
                    icon: 'list-num-default',
                    onAction: function () {
                        editor.execCommand("InsertOrderedList", false, { "list-style-type": "" });
                    },
                }, {
                    type: 'menuitem',
                    text: 'Lower Alpha',
                    icon: 'list-num-lower-alpha',
                    onAction: function() {
                        editor.execCommand("InsertOrderedList", false, { "list-style-type": "lower-alpha" });
                    }
                }, {
                    type: 'menuitem',
                    text: 'Lower Greek',
                    icon: 'list-num-lower-greek',
                    onAction: function() {
                        editor.execCommand("InsertOrderedList", false, { "list-style-type": "lower-greek" });
                    }
                }, {
                    type: 'menuitem',
                    text: 'Lower Roman',
                    icon: 'list-num-lower-roman',
                    onAction: function() {
                        editor.execCommand("InsertOrderedList", false, { "list-style-type": "lower-roman" });
                    }
                }, {
                    type: 'menuitem',
                    text: 'Upper Alpha',
                    icon: 'list-num-upper-alpha',
                    onAction: function() {
                        editor.execCommand("InsertOrderedList", false, { "list-style-type": "upper-alpha" });
                    }
                }, {
                    type: 'menuitem',
                    text: 'Upper Roman',
                    icon: 'list-num-upper-roman',
                    onAction: function() {
                        editor.execCommand("InsertOrderedList", false, { "list-style-type": "upper-roman" });
                    }
                }];
            }
        });

        editor.ui.registry.addNestedMenuItem('covibulletlist', {
            text: tinymce.util.I18n.translate("Bullet list"),
            getSubmenuItems: function() {
                return [{
                    type: 'menuitem',
                    text: 'Default',
                    icon: 'list-bull-default',
                    onAction: function () {
                        editor.execCommand("InsertUnorderedList", false, { "list-style-type": "" });
                    },
                }, {
                    type: 'menuitem',
                    text: 'Circle',
                    icon: 'list-bull-circle',
                    onAction: function() {
                        editor.execCommand("InsertUnorderedList", false, { "list-style-type": "circle" });
                    }
                }, {
                    type: 'menuitem',
                    text: 'Square',
                    icon: 'list-bull-square',
                    onAction: function() {
                        editor.execCommand("InsertUnorderedList", false, { "list-style-type": "square" });
                    }
                }];
            }
        });

        var isList = function(elem) {
            var name = elem.nodeName.toLowerCase();

            return (name === 'ul' || name === 'ol' || name === 'dl');
        }
        var isListItem = function(elem) {
            var name = elem.nodeName.toLowerCase();

            return (name === 'li' || name === 'dt' || name === 'dd');
        }

        editor.on('BeforeExecCommand', function(e) {
            if (e.command === 'InsertOrderedList' || e.command === 'InsertUnorderedList') {
                 var selNode = editor.selection.getNode();
               var selectedNodes = editor.selection.getSelectedBlocks();

               _tool.each(selectedNodes, function(node) {
                  editor.dom.setStyle(node, 'display', null);
               });

               if (editor.dom.getParent(selNode, 'div', editor.dom.getRoot()) && (isList(selNode) || isListItem(selNode))) {
                   editor.dom.setAttrib(selNode, 'data-mce-contenteditable', 'false');
               }
           }
        });

        editor.on('ExecCommand', function(e) {
            if (e.command === 'InsertOrderedList' || e.command === 'InsertUnorderedList') {
                var selNode = editor.selection.getNode();
                var selectedNodes = editor.selection.getSelectedBlocks();

                _tool.each(selectedNodes, function(node) {
                    editor.dom.setStyle(node, 'display', null);
                });

                editor.dom.setAttrib(selNode, 'data-mce-contenteditable', null);
            }
        });

        return {
        };
    });
}());