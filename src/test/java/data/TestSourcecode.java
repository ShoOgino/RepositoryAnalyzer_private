package data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSourcecode {

    @Test
    public void testCalcFanOut1(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                "public Object execute(ExecutionEvent event) throws ExecutionException {\n" +
                        "\tRepositoryNode node = getSelectedNodes(event).get(0);\n" +
                        "\ttry {\n" +
                        "\t\tWizardDialog dlg = new WizardDialog(getShell(event),\n" +
                        "\t\t\t\tnew FetchWizard(node.getRepository()));\n" +
                        "\t\tdlg.setHelpAvailable(false);\n" +
                        "\t\tdlg.open();\n" +
                        "\t} catch (URISyntaxException e1) {\n" +
                        "\t\tActivator.handleError(e1.getMessage(), e1, true);\n" +
                        "\t}\n" +
                        "\treturn null;\n" +
                        "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcFanOut();
        assertEquals(10, sourcecode.fanOut);
    }
    @Test
    public void testCalcFanOut2(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata=
                "/**\n" +
                " * @param repository\n" +
                " * @param state\n" +
                " */\n" +
                "public static void persistState(Repository repository,\n" +
                "\t\tCommitMessageComponentState state) {\n" +
                "\tIDialogSettings dialogSettings = getDialogSettings();\n" +
                "\tString[] values = new String[] { Boolean.toString(state.getAmend()),\n" +
                "\t\t\tstate.getAuthor(), state.getCommitMessage(),\n" +
                "\t\t\tstate.getCommitter(), state.getHeadCommit().getName().toString() };\n" +
                "\tdialogSettings.put(repository.getDirectory().getAbsolutePath(), values);\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcFanOut();
        assertEquals(12, sourcecode.fanOut);
    }
    @Test
    public void testCalcFanOut3(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata=
                "public Object execute(ExecutionEvent event) throws ExecutionException {\n" +
                        "    Repository[] repos = getRepositories(event);\n" +
                        "    if (repos.length == 0)\n" +
                        "        return null;\n" +
                        "    GitSynchronizeDataSet gsdSet = new GitSynchronizeDataSet();\n" +
                        "    for (Repository repo : repos) {\n" +
                        "        try {\n" +
                        "            List<SyncRepoEntity> syncRepoEntitys = createSyncRepoEntitys(repo);\n" +
                        "            SelectSynchronizeResourceDialog dialog = new SelectSynchronizeResourceDialog(\n" +
                        "                    getShell(event), repo.getDirectory(), syncRepoEntitys);\n" +
                        "            if (dialog.open() != IDialogConstants.OK_ID)\n" +
                        "                return null;\n" +
                        "            gsdSet.add(new GitSynchronizeData(repo, dialog.getSrcRef(),\n" +
                        "                    dialog.getDstRef(), dialog.shouldIncludeLocal()));\n" +
                        "        } catch (URISyntaxException e) {\n" +
                        "            Activator.handleError(e.getMessage(), e, true);\n" +
                        "        } catch (IOException e) {\n" +
                        "            Activator.handleError(e.getMessage(), e, true);\n" +
                        "        }\n" +
                        "    }\n" +
                        "    GitModelSynchronize.launch(gsdSet, getSelectedResources(event));\n" +
                        "    return null;\n" +
                        "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcFanOut();
        assertEquals(18, sourcecode.fanOut);
    }
    @Test
    public void testCalcFanOut4(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                "/**\n" +
                " * @param element\n" +
                " * @param state\n" +
                " */\n" +
                "protected void updateCheckState(final Object element, final boolean state) {\n" +
                "    if (state) {\n" +
                "        // Add the item (or its children) to the cache\n" +
                "        ITreeContentProvider contentProvider = null;\n" +
                "        if (getContentProvider() instanceof ITreeContentProvider) {\n" +
                "            contentProvider = (ITreeContentProvider) getContentProvider();\n" +
                "        }\n" +
                "        if (contentProvider != null) {\n" +
                "            Object[] children = contentProvider.getChildren(element);\n" +
                "            if (children != null && children.length > 0) {\n" +
                "                for (int i = 0; i < children.length; i++) {\n" +
                "                    updateCheckState(children[i], state);\n" +
                "                }\n" +
                "            } else {\n" +
                "                checkState.add(element);\n" +
                "            }\n" +
                "        } else {\n" +
                "            checkState.add(element);\n" +
                "        }\n" +
                "    } else if (checkState != null) {\n" +
                "        // Remove the item (or its children) from the cache\n" +
                "        ITreeContentProvider contentProvider = null;\n" +
                "        if (getContentProvider() instanceof ITreeContentProvider) {\n" +
                "            contentProvider = (ITreeContentProvider) getContentProvider();\n" +
                "        }\n" +
                "        if (contentProvider != null) {\n" +
                "            Object[] children = contentProvider.getChildren(element);\n" +
                "            if (children !=null && children.length > 0) {\n" +
                "                for (int i = 0; i < children.length; i++) {\n" +
                "                    updateCheckState(children[i], state);\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        checkState.remove(element);\n" +
                "    }\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcFanOut();
        assertEquals(11, sourcecode.fanOut);
    }
    @Test
    public void testCalcFanOut5(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                        "private List<IPath> getPathList() throws IOException {\n" +
                        "    List<IPath> result = new ArrayList<IPath>();\n" +
                        "    Map<String, Ref> refsMap = getRepository().getRefDatabase().getRefs(\n" +
                        "            getObject().toPortableString()); // getObject() returns path ending with /\n" +
                        "    for (Map.Entry<String, Ref> entry : refsMap.entrySet()) {\n" +
                        "        if (entry.getValue().isSymbolic())\n" +
                        "            continue;\n" +
                        "        result.add(getObject().append(new Path(entry.getKey())));\n" +
                        "    }\n" +
                        "    return result;\n" +
                        "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcFanOut();
        assertEquals(14, sourcecode.fanOut);
    }

    @Test
    public void testCalcLocalVar1(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                        "@Override\n" +
                        "protected Control createDialogArea(Composite parent) {\n" +
                        "    boolean advancedMode = Activator.getDefault().getPreferenceStore()\n" +
                        "            .getBoolean(ADVANCED_MODE_PREFERENCE);\n" +
                        "    final Composite main = new Composite(parent, SWT.NONE);\n" +
                        "    main.setLayout(new GridLayout(1, false));\n" +
                        "    GridDataFactory.fillDefaults().grab(true, true).applyTo(main);\n" +
                        "    Composite repositoryGroup = new Composite(main, SWT.SHADOW_ETCHED_IN);\n" +
                        "    repositoryGroup.setLayout(new GridLayout(2, false));\n" +
                        "    GridDataFactory.fillDefaults().grab(true, true)\n" +
                        "            .applyTo(repositoryGroup);\n" +
                        "    Label repositoryLabel = new Label(repositoryGroup, SWT.NONE);\n" +
                        "    repositoryLabel\n" +
                        "            .setText(UIText.SimpleConfigurePushDialog_RepositoryLabel);\n" +
                        "    Text repositoryText = new Text(repositoryGroup, SWT.BORDER\n" +
                        "            | SWT.READ_ONLY);\n" +
                        "    GridDataFactory.fillDefaults().grab(true, false)\n" +
                        "            .applyTo(repositoryText);\n" +
                        "    repositoryText.setText(Activator.getDefault().getRepositoryUtil()\n" +
                        "            .getRepositoryName(repository));\n" +
                        "    if (showBranchInfo) {\n" +
                        "        Label branchLabel = new Label(repositoryGroup, SWT.NONE);\n" +
                        "        branchLabel.setText(UIText.SimpleConfigurePushDialog_BranchLabel);\n" +
                        "        String branch;\n" +
                        "        try {\n" +
                        "            branch = repository.getBranch();\n" +
                        "        } catch (IOException e2) {\n" +
                        "            branch = null;\n" +
                        "        }\n" +
                        "        if (branch == null || ObjectId.isId(branch)) {\n" +
                        "            branch = UIText.SimpleConfigurePushDialog_DetachedHeadMessage;\n" +
                        "        }\n" +
                        "        Text branchText = new Text(repositoryGroup, SWT.BORDER\n" +
                        "                | SWT.READ_ONLY);\n" +
                        "        GridDataFactory.fillDefaults().grab(true, false)\n" +
                        "                .applyTo(branchText);\n" +
                        "        branchText.setText(branch);\n" +
                        "    }\n" +
                        "    Group remoteGroup = new Group(main, SWT.SHADOW_ETCHED_IN);\n" +
                        "    remoteGroup.setLayout(new GridLayout());\n" +
                        "    GridDataFactory.fillDefaults().grab(true, true).applyTo(remoteGroup);\n" +
                        "    remoteGroup.setText(NLS.bind(\n" +
                        "            UIText.SimpleConfigurePushDialog_RemoteGroupTitle, config\n" +
                        "                    .getName()));\n" +
                        "    addDefaultOriginWarningIfNeeded(remoteGroup);\n" +
                        "    Group uriGroup = new Group(remoteGroup, SWT.SHADOW_ETCHED_IN);\n" +
                        "    uriGroup.setLayout(new GridLayout(1, false));\n" +
                        "    GridDataFactory.fillDefaults().grab(true, true).applyTo(uriGroup);\n" +
                        "    uriGroup.setText(UIText.SimpleConfigurePushDialog_UriGroup);\n" +
                        "    final Composite sameUriDetails = new Composite(uriGroup, SWT.NONE);\n" +
                        "    sameUriDetails.setLayout(new GridLayout(4, false));\n" +
                        "    GridDataFactory.fillDefaults().grab(true, false)\n" +
                        "            .applyTo(sameUriDetails);\n" +
                        "    Label commonUriLabel = new Label(sameUriDetails, SWT.NONE);\n" +
                        "    commonUriLabel.setText(UIText.SimpleConfigurePushDialog_URILabel);\n" +
                        "    commonUriText = new Text(sameUriDetails, SWT.BORDER | SWT.READ_ONLY);\n" +
                        "    GridDataFactory.fillDefaults().grab(true, false).applyTo(commonUriText);\n" +
                        "    changeCommonUri = new Button(sameUriDetails, SWT.PUSH);\n" +
                        "    changeCommonUri\n" +
                        "            .setText(UIText.SimpleConfigurePushDialog_ChangeUriButton);\n" +
                        "    changeCommonUri.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            SelectUriWizard wiz;\n" +
                        "            if (commonUriText.getText().length() > 0)\n" +
                        "                wiz = new SelectUriWizard(false, commonUriText.getText());\n" +
                        "            else\n" +
                        "                wiz = new SelectUriWizard(false);\n" +
                        "            if (new WizardDialog(getShell(), wiz).open() == Window.OK) {\n" +
                        "                if (commonUriText.getText().length() > 0)\n" +
                        "                    try {\n" +
                        "                        config\n" +
                        "                                .removeURI(new URIish(commonUriText\n" +
                        "                                        .getText()));\n" +
                        "                    } catch (URISyntaxException ex) {\n" +
                        "                        Activator.handleError(ex.getMessage(), ex, true);\n" +
                        "                    }\n" +
                        "                config.addURI(wiz.getUri());\n" +
                        "                updateControls();\n" +
                        "            }\n" +
                        "        }\n" +
                        "    });\n" +
                        "    deleteCommonUri = new Button(sameUriDetails, SWT.PUSH);\n" +
                        "    deleteCommonUri\n" +
                        "            .setText(UIText.SimpleConfigurePushDialog_DeleteUriButton);\n" +
                        "    deleteCommonUri.setEnabled(false);\n" +
                        "    deleteCommonUri.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            config.removeURI(config.getURIs().get(0));\n" +
                        "            updateControls();\n" +
                        "        }\n" +
                        "    });\n" +
                        "    commonUriText.addModifyListener(new ModifyListener() {\n" +
                        "        public void modifyText(ModifyEvent e) {\n" +
                        "            deleteCommonUri\n" +
                        "                    .setEnabled(commonUriText.getText().length() > 0);\n" +
                        "        }\n" +
                        "    });\n" +
                        "    final Composite pushUriDetails = new Composite(uriGroup, SWT.NONE);\n" +
                        "    pushUriDetails.setLayout(new GridLayout(3, false));\n" +
                        "    GridDataFactory.fillDefaults().grab(true, true).applyTo(pushUriDetails);\n" +
                        "    Label urisLabel = new Label(pushUriDetails, SWT.NONE);\n" +
                        "    urisLabel.setText(UIText.SimpleConfigurePushDialog_PushUrisLabel);\n" +
                        "    GridDataFactory.fillDefaults().span(3, 1).applyTo(urisLabel);\n" +
                        "    uriViewer = new TableViewer(pushUriDetails, SWT.BORDER | SWT.MULTI);\n" +
                        "    GridDataFactory.fillDefaults().grab(true, true).span(3, 1).minSize(\n" +
                        "            SWT.DEFAULT, 30).applyTo(uriViewer.getTable());\n" +
                        "    uriViewer.setContentProvider(ArrayContentProvider.getInstance());\n" +
                        "    Button addUri = new Button(pushUriDetails, SWT.PUSH);\n" +
                        "    addUri.setText(UIText.SimpleConfigurePushDialog_AddPushUriButton);\n" +
                        "    addUri.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            SelectUriWizard wiz = new SelectUriWizard(false);\n" +
                        "            if (new WizardDialog(getShell(), wiz).open() == Window.OK) {\n" +
                        "                config.addPushURI(wiz.getUri());\n" +
                        "                updateControls();\n" +
                        "            }\n" +
                        "        }\n" +
                        "    });\n" +
                        "    final Button changeUri = new Button(pushUriDetails, SWT.PUSH);\n" +
                        "    changeUri.setText(UIText.SimpleConfigurePushDialog_ChangePushUriButton);\n" +
                        "    changeUri.setEnabled(false);\n" +
                        "    changeUri.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            URIish uri = (URIish) ((IStructuredSelection) uriViewer\n" +
                        "                    .getSelection()).getFirstElement();\n" +
                        "            SelectUriWizard wiz = new SelectUriWizard(false, uri\n" +
                        "                    .toPrivateString());\n" +
                        "            if (new WizardDialog(getShell(), wiz).open() == Window.OK) {\n" +
                        "                config.removePushURI(uri);\n" +
                        "                config.addPushURI(wiz.getUri());\n" +
                        "                updateControls();\n" +
                        "            }\n" +
                        "        }\n" +
                        "    });\n" +
                        "    final Button deleteUri = new Button(pushUriDetails, SWT.PUSH);\n" +
                        "    deleteUri.setText(UIText.SimpleConfigurePushDialog_DeletePushUriButton);\n" +
                        "    deleteUri.setEnabled(false);\n" +
                        "    deleteUri.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            URIish uri = (URIish) ((IStructuredSelection) uriViewer\n" +
                        "                    .getSelection()).getFirstElement();\n" +
                        "            config.removePushURI(uri);\n" +
                        "            updateControls();\n" +
                        "        }\n" +
                        "    });\n" +
                        "    uriViewer.addSelectionChangedListener(new ISelectionChangedListener() {\n" +
                        "        public void selectionChanged(SelectionChangedEvent event) {\n" +
                        "            deleteUri.setEnabled(!uriViewer.getSelection().isEmpty());\n" +
                        "            changeUri.setEnabled(((IStructuredSelection) uriViewer\n" +
                        "                    .getSelection()).size() == 1);\n" +
                        "        }\n" +
                        "    });\n" +
                        "    final Group refSpecGroup = new Group(remoteGroup, SWT.SHADOW_ETCHED_IN);\n" +
                        "    GridDataFactory.fillDefaults().grab(true, true).applyTo(refSpecGroup);\n" +
                        "    refSpecGroup.setText(UIText.SimpleConfigurePushDialog_RefMappingGroup);\n" +
                        "    refSpecGroup.setLayout(new GridLayout(5, false));\n" +
                        "    ExpandableComposite advanced = new ExpandableComposite(refSpecGroup,\n" +
                        "            ExpandableComposite.TREE_NODE\n" +
                        "                    | ExpandableComposite.CLIENT_INDENT);\n" +
                        "    if (advancedMode)\n" +
                        "        advanced.setExpanded(true);\n" +
                        "    advanced.setText(UIText.SimpleConfigurePushDialog_AdvancedButton);\n" +
                        "    GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.FILL)\n" +
                        "            .span(5, 1).grab(true, false).applyTo(advanced);\n" +
                        "    advanced.addExpansionListener(new ExpansionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void expansionStateChanged(ExpansionEvent e) {\n" +
                        "            Activator.getDefault().getPreferenceStore().setValue(\n" +
                        "                    ADVANCED_MODE_PREFERENCE, e.getState());\n" +
                        "            GridData data = (GridData) changeRefSpec.getLayoutData();\n" +
                        "            data.exclude = !e.getState();\n" +
                        "            changeRefSpec.setVisible(!data.exclude);\n" +
                        "            refSpecGroup.layout(true);\n" +
                        "        }\n" +
                        "    });\n" +
                        "    Label refSpecLabel = new Label(refSpecGroup, SWT.NONE);\n" +
                        "    refSpecLabel.setText(UIText.SimpleConfigurePushDialog_RefSpecLabel);\n" +
                        "    GridDataFactory.fillDefaults().span(5, 1).applyTo(refSpecLabel);\n" +
                        "    specViewer = new TableViewer(refSpecGroup, SWT.BORDER | SWT.MULTI);\n" +
                        "    specViewer.setContentProvider(ArrayContentProvider.getInstance());\n" +
                        "    GridDataFactory.fillDefaults().span(5, 1).grab(true, true).minSize(\n" +
                        "            SWT.DEFAULT, 30).applyTo(specViewer.getTable());\n" +
                        "    specViewer.getTable().addKeyListener(new KeyAdapter() {\n" +
                        "        @Override\n" +
                        "        public void keyPressed(KeyEvent e) {\n" +
                        "            if (e.stateMask == SWT.MOD1 && e.keyCode == 'v') {\n" +
                        "                doPaste();\n" +
                        "            }\n" +
                        "        }\n" +
                        "    });\n" +
                        "    addRefSpec = new Button(refSpecGroup, SWT.PUSH);\n" +
                        "    addRefSpec.setText(UIText.SimpleConfigurePushDialog_AddRefSpecButton);\n" +
                        "    addRefSpec.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            RefSpecDialog dlg = new RefSpecDialog(getShell(), repository,\n" +
                        "                    config, true);\n" +
                        "            if (dlg.open() == Window.OK) {\n" +
                        "                config.addPushRefSpec(dlg.getSpec());\n" +
                        "            }\n" +
                        "            updateControls();\n" +
                        "        }\n" +
                        "    });\n" +
                        "    changeRefSpec = new Button(refSpecGroup, SWT.PUSH);\n" +
                        "    changeRefSpec\n" +
                        "            .setText(UIText.SimpleConfigurePushDialog_ChangeRefSpecButton);\n" +
                        "    changeRefSpec.setEnabled(false);\n" +
                        "    GridDataFactory.fillDefaults().exclude(!advancedMode).applyTo(\n" +
                        "            changeRefSpec);\n" +
                        "    changeRefSpec.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            RefSpec oldSpec = (RefSpec) ((IStructuredSelection) specViewer\n" +
                        "                    .getSelection()).getFirstElement();\n" +
                        "            RefSpecDialog dlg = new RefSpecDialog(getShell(), repository,\n" +
                        "                    config, oldSpec, true);\n" +
                        "            if (dlg.open() == Window.OK) {\n" +
                        "                config.removePushRefSpec(oldSpec);\n" +
                        "                config.addPushRefSpec(dlg.getSpec());\n" +
                        "            }\n" +
                        "            updateControls();\n" +
                        "        }\n" +
                        "    });\n" +
                        "    final Button deleteRefSpec = new Button(refSpecGroup, SWT.PUSH);\n" +
                        "    deleteRefSpec\n" +
                        "            .setText(UIText.SimpleConfigurePushDialog_DeleteRefSpecButton);\n" +
                        "    deleteRefSpec.setEnabled(false);\n" +
                        "    deleteRefSpec.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            for (Object spec : ((IStructuredSelection) specViewer\n" +
                        "                    .getSelection()).toArray()) {\n" +
                        "                config.removePushRefSpec((RefSpec) spec);\n" +
                        "            }\n" +
                        "            updateControls();\n" +
                        "        }\n" +
                        "    });\n" +
                        "    final Button copySpec = new Button(refSpecGroup, SWT.PUSH);\n" +
                        "    copySpec.setText(UIText.SimpleConfigurePushDialog_CopyRefSpecButton);\n" +
                        "    copySpec.setEnabled(false);\n" +
                        "    copySpec.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            String toCopy = ((IStructuredSelection) specViewer\n" +
                        "                    .getSelection()).getFirstElement().toString();\n" +
                        "            Clipboard clipboard = new Clipboard(getShell().getDisplay());\n" +
                        "            try {\n" +
                        "                clipboard.setContents(new String[] { toCopy },\n" +
                        "                        new TextTransfer[] { TextTransfer.getInstance() });\n" +
                        "            } finally {\n" +
                        "                clipboard.dispose();\n" +
                        "            }\n" +
                        "        }\n" +
                        "    });\n" +
                        "    final Button pasteSpec = new Button(refSpecGroup, SWT.PUSH);\n" +
                        "    pasteSpec.setText(UIText.SimpleConfigurePushDialog_PasteRefSpecButton);\n" +
                        "    pasteSpec.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            doPaste();\n" +
                        "        }\n" +
                        "    });\n" +
                        "    addRefSpecAdvanced = new Button(advanced, SWT.PUSH);\n" +
                        "    advanced.setClient(addRefSpecAdvanced);\n" +
                        "    GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.FILL)\n" +
                        "            .span(3, 1).applyTo(addRefSpecAdvanced);\n" +
                        "    addRefSpecAdvanced\n" +
                        "            .setText(UIText.SimpleConfigurePushDialog_EditAdvancedButton);\n" +
                        "    addRefSpecAdvanced.addSelectionListener(new SelectionAdapter() {\n" +
                        "        @Override\n" +
                        "        public void widgetSelected(SelectionEvent e) {\n" +
                        "            if (new WizardDialog(getShell(), new RefSpecWizard(repository,\n" +
                        "                    config, true)).open() == Window.OK)\n" +
                        "                updateControls();\n" +
                        "        }\n" +
                        "    });\n" +
                        "    specViewer.addSelectionChangedListener(new ISelectionChangedListener() {\n" +
                        "        public void selectionChanged(SelectionChangedEvent event) {\n" +
                        "            IStructuredSelection sel = (IStructuredSelection) specViewer\n" +
                        "                    .getSelection();\n" +
                        "            copySpec.setEnabled(sel.size() == 1);\n" +
                        "            changeRefSpec.setEnabled(sel.size() == 1);\n" +
                        "            deleteRefSpec.setEnabled(!sel.isEmpty());\n" +
                        "        }\n" +
                        "    });\n" +
                        "    applyDialogFont(main);\n" +
                        "    return main;\n" +
                        "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcLocalVar();
        assertEquals(35, sourcecode.localVar);
    }
    @Test
    public void testCalcLocalVar2(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                "private Image decorateImage(final Image image, Object element) {\n" +
                "    RepositoryTreeNode node = (RepositoryTreeNode) element;\n" +
                "    switch (node.getType()) {\n" +
                "    case TAG:\n" +
                "        // fall through\n" +
                "    case ADDITIONALREF:\n" +
                "        // fall through\n" +
                "    case REF:\n" +
                "        // if the branch or tag is checked out,\n" +
                "        // we want to decorate the corresponding\n" +
                "        // node with a little check indicator\n" +
                "        String refName = ((Ref) node.getObject()).getName();\n" +
                "        Ref leaf = ((Ref) node.getObject()).getLeaf();\n" +
                "        String branchName;\n" +
                "        String compareString;\n" +
                "        try {\n" +
                "            branchName = node.getRepository().getFullBranch();\n" +
                "            if (branchName == null)\n" +
                "                return image;\n" +
                "            if (refName.startsWith(Constants.R_HEADS)) {\n" +
                "                // local branch: HEAD would be on the branch\n" +
                "                compareString = refName;\n" +
                "            } else if (refName.startsWith(Constants.R_TAGS)) {\n" +
                "                // tag: HEAD would be on the commit id to which the tag is\n" +
                "                // pointing\n" +
                "                ObjectId id = node.getRepository().resolve(refName);\n" +
                "                if (id == null)\n" +
                "                    return image;\n" +
                "                RevWalk rw = new RevWalk(node.getRepository());\n" +
                "                RevTag tag = rw.parseTag(id);\n" +
                "                compareString = tag.getObject().name();\n" +
                "            } else if (refName.startsWith(Constants.R_REMOTES)) {\n" +
                "                // remote branch: HEAD would be on the commit id to which\n" +
                "                // the branch is pointing\n" +
                "                ObjectId id = node.getRepository().resolve(refName);\n" +
                "                if (id == null)\n" +
                "                    return image;\n" +
                "                RevWalk rw = new RevWalk(node.getRepository());\n" +
                "                RevCommit commit = rw.parseCommit(id);\n" +
                "                compareString = commit.getId().name();\n" +
                "            } else if (refName.equals(Constants.HEAD))\n" +
                "                return getDecoratedImage(image);\n" +
                "            else {\n" +
                "                String leafname = leaf.getName();\n" +
                "                if (leafname.startsWith(Constants.R_REFS)\n" +
                "                        && leafname.equals(node.getRepository()\n" +
                "                                .getFullBranch()))\n" +
                "                    return getDecoratedImage(image);\n" +
                "                else if (leaf.getObjectId().equals(\n" +
                "                        node.getRepository().resolve(Constants.HEAD)))\n" +
                "                    return getDecoratedImage(image);\n" +
                "                // some other symbolic reference\n" +
                "                return image;\n" +
                "            }\n" +
                "        } catch (IOException e1) {\n" +
                "            return image;\n" +
                "        }\n" +
                "        if (compareString.equals(branchName)) {\n" +
                "            return getDecoratedImage(image);\n" +
                "        }\n" +
                "        return image;\n" +
                "    default:\n" +
                "        return image;\n" +
                "    }\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcLocalVar();
        assertEquals(12, sourcecode.localVar);
    }
    @Test
    public void testCalcLocalVar3(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                "private ArrayList<String> buildFilterPaths(final IResource[] inResources,\n" +
                "        final File[] inFiles, final Repository db)\n" +
                "        throws IllegalStateException {\n" +
                "    final ArrayList<String> paths;\n" +
                "    if (inResources != null) {\n" +
                "        paths = new ArrayList<String>(inResources.length);\n" +
                "        for (final IResource r : inResources) {\n" +
                "            final RepositoryMapping map = RepositoryMapping.getMapping(r);\n" +
                "            if (map == null)\n" +
                "                continue;\n" +
                "            if (db != map.getRepository()) {\n" +
                "                throw new IllegalStateException(\n" +
                "                        UIText.AbstractHistoryCommanndHandler_NoUniqueRepository);\n" +
                "            }\n" +
                "            if (showAllFilter == ShowFilter.SHOWALLFOLDER) {\n" +
                "                final String path;\n" +
                "                // if the resource's parent is the workspace root, we will\n" +
                "                // get nonsense from map.getRepoRelativePath(), so we\n" +
                "                // check here and use the project instead\n" +
                "                if (r.getParent() instanceof IWorkspaceRoot)\n" +
                "                    path = map.getRepoRelativePath(r.getProject());\n" +
                "                else\n" +
                "                    path = map.getRepoRelativePath(r.getParent());\n" +
                "                if (path != null && path.length() > 0)\n" +
                "                    paths.add(path);\n" +
                "            } else if (showAllFilter == ShowFilter.SHOWALLPROJECT) {\n" +
                "                final String path = map.getRepoRelativePath(r.getProject());\n" +
                "                if (path != null && path.length() > 0)\n" +
                "                    paths.add(path);\n" +
                "            } else if (showAllFilter == ShowFilter.SHOWALLREPO) {\n" +
                "                // nothing\n" +
                "            } else /* if (showAllFilter == ShowFilter.SHOWALLRESOURCE) */{\n" +
                "                final String path = map.getRepoRelativePath(r);\n" +
                "                if (path != null && path.length() > 0)\n" +
                "                    paths.add(path);\n" +
                "            }\n" +
                "        }\n" +
                "    } else if (inFiles != null) {\n" +
                "        IPath workdirPath = new Path(db.getWorkTree().getPath());\n" +
                "        IPath gitDirPath = new Path(db.getDirectory().getPath());\n" +
                "        int segmentCount = workdirPath.segmentCount();\n" +
                "        paths = new ArrayList<String>(inFiles.length);\n" +
                "        for (File file : inFiles) {\n" +
                "            IPath filePath;\n" +
                "            if (showAllFilter == ShowFilter.SHOWALLFOLDER) {\n" +
                "                filePath = new Path(file.getParentFile().getPath());\n" +
                "            } else if (showAllFilter == ShowFilter.SHOWALLPROJECT\n" +
                "                    || showAllFilter == ShowFilter.SHOWALLREPO) {\n" +
                "                // we don't know of projects here -> treat as SHOWALLREPO\n" +
                "                continue;\n" +
                "            } else /* if (showAllFilter == ShowFilter.SHOWALLRESOURCE) */{\n" +
                "                filePath = new Path(file.getPath());\n" +
                "            }\n" +
                "            if (gitDirPath.isPrefixOf(filePath)) {\n" +
                "                throw new IllegalStateException(\n" +
                "                        NLS\n" +
                "                                .bind(\n" +
                "                                        UIText.GitHistoryPage_FileOrFolderPartOfGitDirMessage,\n" +
                "                                        filePath.toOSString()));\n" +
                "            }\n" +
                "            IPath pathToAdd = filePath.removeFirstSegments(segmentCount)\n" +
                "                    .setDevice(null);\n" +
                "            if (!pathToAdd.isEmpty()) {\n" +
                "                paths.add(pathToAdd.toString());\n" +
                "            }\n" +
                "        }\n" +
                "    } else {\n" +
                "        paths = new ArrayList<String>(0);\n" +
                "    }\n" +
                "    return paths;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcLocalVar();
        assertEquals(10, sourcecode.localVar);
    }
    @Test
    public void testCalcLocalVar4(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private void reactOnOpen(OpenEvent event) {\n" +
                "    Object selected = ((IStructuredSelection) event.getSelection())\n" +
                "            .getFirstElement();\n" +
                "    ITypedElement left;\n" +
                "    ITypedElement right;\n" +
                "    if (selected instanceof IContainer) {\n" +
                "        // open/close folder\n" +
                "        TreeViewer tv = (TreeViewer) event.getViewer();\n" +
                "        tv.setExpandedState(selected, !tv.getExpandedState(selected));\n" +
                "        return;\n" +
                "    } else if (selected instanceof IFile) {\n" +
                "        final IFile res = (IFile) selected;\n" +
                "        left = new EditableRevision(new LocalFileRevision(res)) {\n" +
                "            @Override\n" +
                "            public void setContent(final byte[] newContent) {\n" +
                "                try {\n" +
                "                    PlatformUI.getWorkbench().getProgressService().run(\n" +
                "                            false, false, new IRunnableWithProgress() {\n" +
                "                                public void run(IProgressMonitor myMonitor)\n" +
                "                                        throws InvocationTargetException,\n" +
                "                                        InterruptedException {\n" +
                "                                    try {\n" +
                "                                        res.setContents(\n" +
                "                                                new ByteArrayInputStream(\n" +
                "                                                        newContent), false,\n" +
                "                                                true, myMonitor);\n" +
                "                                    } catch (CoreException e) {\n" +
                "                                        throw new InvocationTargetException(\n" +
                "                                                e);\n" +
                "                                    }\n" +
                "                                }\n" +
                "                            });\n" +
                "                } catch (InvocationTargetException e) {\n" +
                "                    Activator.handleError(e.getTargetException()\n" +
                "                            .getMessage(), e.getTargetException(), true);\n" +
                "                } catch (InterruptedException e) {\n" +
                "                    // ignore here\n" +
                "                }\n" +
                "            }\n" +
                "        };\n" +
                "        GitFileRevision rightRevision = compareVersionMap.get(new Path(\n" +
                "                repositoryMapping.getRepoRelativePath(res)));\n" +
                "        if (rightRevision == null)\n" +
                "            right = new GitCompareFileRevisionEditorInput.EmptyTypedElement(\n" +
                "                    NLS\n" +
                "                            .bind(\n" +
                "                                    UIText.CompareTreeView_ItemNotFoundInVersionMessage,\n" +
                "                                    res.getName(), getCompareVersion()));\n" +
                "        else\n" +
                "            right = new FileRevisionTypedElement(rightRevision);\n" +
                "        GitCompareFileRevisionEditorInput compareInput = new GitCompareFileRevisionEditorInput(\n" +
                "                left, right, PlatformUI.getWorkbench()\n" +
                "                        .getActiveWorkbenchWindow().getActivePage());\n" +
                "        CompareUtils.openInCompare(PlatformUI.getWorkbench()\n" +
                "                .getActiveWorkbenchWindow().getActivePage(), compareInput);\n" +
                "    } else if (selected instanceof GitFileRevision) {\n" +
                "        GitFileRevision rightRevision = (GitFileRevision) selected;\n" +
                "        left = new GitCompareFileRevisionEditorInput.EmptyTypedElement(NLS\n" +
                "                .bind(UIText.CompareTreeView_ItemNotFoundInVersionMessage,\n" +
                "                        rightRevision.getName(), getBaseVersion()));\n" +
                "        right = new FileRevisionTypedElement(rightRevision);\n" +
                "    } else if (selected instanceof PathNode) {\n" +
                "        PathNode node = (PathNode) selected;\n" +
                "        switch (node.type) {\n" +
                "        case FILE_BOTH_SIDES_DIFFER:\n" +
                "            // fall through\n" +
                "        case FILE_BOTH_SIDES_SAME: {\n" +
                "            // open a compare editor with both sides filled\n" +
                "            GitFileRevision rightRevision = compareVersionMap\n" +
                "                    .get(node.path);\n" +
                "            right = new FileRevisionTypedElement(rightRevision);\n" +
                "            GitFileRevision leftRevision = baseVersionMap.get(node.path);\n" +
                "            left = new FileRevisionTypedElement(leftRevision);\n" +
                "            break;\n" +
                "        }\n" +
                "        case FILE_DELETED: {\n" +
                "            // open compare editor with left side empty\n" +
                "            GitFileRevision rightRevision = compareVersionMap\n" +
                "                    .get(node.path);\n" +
                "            right = new FileRevisionTypedElement(rightRevision);\n" +
                "            left = new GitCompareFileRevisionEditorInput.EmptyTypedElement(\n" +
                "                    NLS\n" +
                "                            .bind(\n" +
                "                                    UIText.CompareTreeView_ItemNotFoundInVersionMessage,\n" +
                "                                    rightRevision.getName(),\n" +
                "                                    getBaseVersion()));\n" +
                "            break;\n" +
                "        }\n" +
                "        case FILE_ADDED: {\n" +
                "            // open compare editor with right side empty\n" +
                "            GitFileRevision leftRevision = baseVersionMap.get(node.path);\n" +
                "            left = new FileRevisionTypedElement(leftRevision);\n" +
                "            right = new GitCompareFileRevisionEditorInput.EmptyTypedElement(\n" +
                "                    NLS\n" +
                "                            .bind(\n" +
                "                                    UIText.CompareTreeView_ItemNotFoundInVersionMessage,\n" +
                "                                    leftRevision.getName(),\n" +
                "                                    getCompareVersion()));\n" +
                "            break;\n" +
                "        }\n" +
                "        case FOLDER:\n" +
                "            // open/close folder\n" +
                "            TreeViewer tv = (TreeViewer) event.getViewer();\n" +
                "            tv.setExpandedState(selected, !tv.getExpandedState(selected));\n" +
                "            return;\n" +
                "        default:\n" +
                "            return;\n" +
                "        }\n" +
                "    } else if (selected instanceof PathNodeAdapter) {\n" +
                "        // deleted in workspace\n" +
                "        PathNodeAdapter node = (PathNodeAdapter) selected;\n" +
                "        GitFileRevision rightRevision = compareVersionMap\n" +
                "                .get(node.pathNode.path);\n" +
                "        right = new FileRevisionTypedElement(rightRevision);\n" +
                "        left = new GitCompareFileRevisionEditorInput.EmptyTypedElement(NLS\n" +
                "                .bind(UIText.CompareTreeView_ItemNotFoundInVersionMessage,\n" +
                "                        node.pathNode.path.lastSegment(), getBaseVersion()));\n" +
                "    } else\n" +
                "        return;\n" +
                "    GitCompareFileRevisionEditorInput compareInput = new GitCompareFileRevisionEditorInput(\n" +
                "            left, right, PlatformUI.getWorkbench()\n" +
                "                    .getActiveWorkbenchWindow().getActivePage());\n" +
                "    CompareUtils.openInCompare(PlatformUI.getWorkbench()\n" +
                "            .getActiveWorkbenchWindow().getActivePage(), compareInput);\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcLocalVar();
        assertEquals(17, sourcecode.localVar);
    }
    @Test
    public void testCalcLocalVar5(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                "/**\n" +
                " * @param event\n" +
                " *            the {@link ExecutionEvent}\n" +
                " * @return a {@link Repository} if all elements in the current selection map\n" +
                " *         to the same {@link Repository}, otherwise null\n" +
                " */\n" +
                "protected Repository getRepository(ExecutionEvent event) {\n" +
                "    ISelection selection = HandlerUtil.getCurrentSelection(event);\n" +
                "    if (selection.isEmpty())\n" +
                "        return null;\n" +
                "    if (selection instanceof IStructuredSelection) {\n" +
                "        IStructuredSelection ssel = (IStructuredSelection) selection;\n" +
                "        Repository result = null;\n" +
                "        for (Object element : ssel.toList()) {\n" +
                "            Repository elementRepository = null;\n" +
                "            if (element instanceof RepositoryTreeNode) {\n" +
                "                elementRepository = ((RepositoryTreeNode) element)\n" +
                "                        .getRepository();\n" +
                "            } else if (element instanceof IResource) {\n" +
                "                IResource resource = (IResource) element;\n" +
                "                RepositoryMapping mapping = RepositoryMapping\n" +
                "                        .getMapping(resource.getProject());\n" +
                "                if (mapping != null)\n" +
                "                    elementRepository = mapping.getRepository();\n" +
                "            } else if (element instanceof IAdaptable) {\n" +
                "                IResource adapted = (IResource) ((IAdaptable) element)\n" +
                "                        .getAdapter(IResource.class);\n" +
                "                if (adapted != null) {\n" +
                "                    RepositoryMapping mapping = RepositoryMapping\n" +
                "                            .getMapping(adapted.getProject());\n" +
                "                    if (mapping != null)\n" +
                "                        elementRepository = mapping.getRepository();\n" +
                "                }\n" +
                "            }\n" +
                "            if (elementRepository == null)\n" +
                "                continue;\n" +
                "            if (result != null && !elementRepository.equals(result))\n" +
                "                return null;\n" +
                "            if (result == null)\n" +
                "                result = elementRepository;\n" +
                "        }\n" +
                "        return result;\n" +
                "    }\n" +
                "    if (selection instanceof TextSelection) {\n" +
                "        IEditorInput activeEditor = WORKBENCH.getActiveWorkbenchWindow()\n" +
                "                .getActivePage().getActiveEditor().getEditorInput();\n" +
                "        IResource resource = (IResource) activeEditor\n" +
                "                .getAdapter(IResource.class);\n" +
                "        if (resource != null)\n" +
                "            return RepositoryMapping.getMapping(resource).getRepository();\n" +
                "    }\n" +
                "    return null;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcLocalVar();
        assertEquals(10, sourcecode.localVar);
    }

    @Test
    public void testCalcParameters1() {
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                "private List<IPath> getPathList() throws IOException {\n" +
                        "    List<IPath> result = new ArrayList<IPath>();\n" +
                        "    Map<String, Ref> refsMap = getRepository().getRefDatabase().getRefs(\n" +
                        "            getObject().toPortableString()); // getObject() returns path ending with /\n" +
                        "    for (Map.Entry<String, Ref> entry : refsMap.entrySet()) {\n" +
                        "        if (entry.getValue().isSymbolic())\n" +
                        "            continue;\n" +
                        "        result.add(getObject().append(new Path(entry.getKey())));\n" +
                        "    }\n" +
                        "    return result;\n" +
                        "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcParameters();
        assertEquals(0, sourcecode.fanOut);
    }
    @Test
    public void testCalcParameters2(){
        Sourcecode sourcecode=new Sourcecode();
        sourcecode.rawdata =
                "/**\n" +
                        " * @param event\n" +
                        " *            the {@link ExecutionEvent}\n" +
                        " * @return a {@link Repository} if all elements in the current selection map\n" +
                        " *         to the same {@link Repository}, otherwise null\n" +
                        " */\n" +
                        "protected Repository getRepository(ExecutionEvent event) {\n" +
                        "    ISelection selection = HandlerUtil.getCurrentSelection(event);\n" +
                        "    if (selection.isEmpty())\n" +
                        "        return null;\n" +
                        "    if (selection instanceof IStructuredSelection) {\n" +
                        "        IStructuredSelection ssel = (IStructuredSelection) selection;\n" +
                        "        Repository result = null;\n" +
                        "        for (Object element : ssel.toList()) {\n" +
                        "            Repository elementRepository = null;\n" +
                        "            if (element instanceof RepositoryTreeNode) {\n" +
                        "                elementRepository = ((RepositoryTreeNode) element)\n" +
                        "                        .getRepository();\n" +
                        "            } else if (element instanceof IResource) {\n" +
                        "                IResource resource = (IResource) element;\n" +
                        "                RepositoryMapping mapping = RepositoryMapping\n" +
                        "                        .getMapping(resource.getProject());\n" +
                        "                if (mapping != null)\n" +
                        "                    elementRepository = mapping.getRepository();\n" +
                        "            } else if (element instanceof IAdaptable) {\n" +
                        "                IResource adapted = (IResource) ((IAdaptable) element)\n" +
                        "                        .getAdapter(IResource.class);\n" +
                        "                if (adapted != null) {\n" +
                        "                    RepositoryMapping mapping = RepositoryMapping\n" +
                        "                            .getMapping(adapted.getProject());\n" +
                        "                    if (mapping != null)\n" +
                        "                        elementRepository = mapping.getRepository();\n" +
                        "                }\n" +
                        "            }\n" +
                        "            if (elementRepository == null)\n" +
                        "                continue;\n" +
                        "            if (result != null && !elementRepository.equals(result))\n" +
                        "                return null;\n" +
                        "            if (result == null)\n" +
                        "                result = elementRepository;\n" +
                        "        }\n" +
                        "        return result;\n" +
                        "    }\n" +
                        "    if (selection instanceof TextSelection) {\n" +
                        "        IEditorInput activeEditor = WORKBENCH.getActiveWorkbenchWindow()\n" +
                        "                .getActivePage().getActiveEditor().getEditorInput();\n" +
                        "        IResource resource = (IResource) activeEditor\n" +
                        "                .getAdapter(IResource.class);\n" +
                        "        if (resource != null)\n" +
                        "            return RepositoryMapping.getMapping(resource).getRepository();\n" +
                        "    }\n" +
                        "    return null;\n" +
                        "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcParameters();
        assertEquals(1, sourcecode.parameters);
    }
    @Test
    public void testCalcParameters3(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
        "/**\n" +
                " * @param repository\n" +
                " * @param state\n" +
                " */\n" +
                "public static void persistState(Repository repository,\n" +
                "\t\tCommitMessageComponentState state) {\n" +
                "\tIDialogSettings dialogSettings = getDialogSettings();\n" +
                "\tString[] values = new String[] { Boolean.toString(state.getAmend()),\n" +
                "\t\t\tstate.getAuthor(), state.getCommitMessage(),\n" +
                "\t\t\tstate.getCommitter(), state.getHeadCommit().getName().toString() };\n" +
                "\tdialogSettings.put(repository.getDirectory().getAbsolutePath(), values);\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcParameters();
        assertEquals(2, sourcecode.parameters);
    }

    @Test
    public void testCalcCommentRatio1(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/**\n" +
                " * Construct a new iterator from the workspace root.\n" +
                " * <p>\n" +
                " * The iterator will support traversal over workspace projects that have\n" +
                " * a Git repository provider connected and is mapped into a Git repository.\n" +
                " * During the iteration the paths will be automatically generated to match\n" +
                " * the proper repository paths for this container's children.\n" +
                " *\n" +
                " * @param repository\n" +
                " *            repository the given base is mapped to\n" +
                " * @param root\n" +
                " *            the workspace root to walk over.\n" +
                " */\n" +
                "public ContainerTreeIterator(final Repository repository, final IWorkspaceRoot root) {\n" +
                "    super(\"\", repository.getConfig().get(WorkingTreeOptions.KEY));  //$NON-NLS-1$\n" +
                "    node = root;\n" +
                "    init(entries());\n" +
                "    initRootIterator(repository);\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCommentRatio();
        assertEquals(String.format("%.5f", 14/(float)19), String.format("%.5f", sourcecode.commentRatio));
    }
    @Test
    public void testCalcCommentRatio2(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private static TreeWalk createThreeWayTreeWalk(\n" +
                "        final RepositoryMapping mapping,\n" +
                "        final ArrayList<String> resourcePaths) throws IOException {\n" +
                "    final Repository repository = mapping.getRepository();\n" +
                "    final TreeWalk treeWalk = new TreeWalk(repository);\n" +
                "    // Copy path list...\n" +
                "    final ArrayList<String> paths = new ArrayList<String>(resourcePaths);\n" +
                "    while (paths.remove(null)) {\n" +
                "        // ... and remove nulls\n" +
                "    }\n" +
                "    treeWalk.setFilter(PathFilterGroup.createFromStrings(paths));\n" +
                "    treeWalk.setRecursive(true);\n" +
                "    treeWalk.reset();\n" +
                "    // Repository\n" +
                "    final ObjectId headId = repository.resolve(Constants.HEAD);\n" +
                "    if (headId != null)\n" +
                "        treeWalk.addTree(new RevWalk(repository).parseTree(headId));\n" +
                "    else\n" +
                "        treeWalk.addTree(new EmptyTreeIterator());\n" +
                "    // Index\n" +
                "    treeWalk.addTree(new DirCacheIterator(getDirCache(repository)));\n" +
                "    // Working directory\n" +
                "    treeWalk.addTree(IteratorService.createInitialIterator(repository));\n" +
                "    return treeWalk;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCommentRatio();
        assertEquals(String.format("%.5f",0.20000) , String.format("%.5f", sourcecode.commentRatio));
    }
    @Test
    public void testCalcCommentRatio3(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private String getSimpleText(RepositoryTreeNode node) {\n" +
                "    switch (node.getType()) {\n" +
                "    case REPO:\n" +
                "        File directory = ((Repository) node.getObject()).getDirectory();\n" +
                "        StringBuilder sb = new StringBuilder();\n" +
                "        sb.append(directory.getParentFile().getName());\n" +
                "        sb.append(\" - \"); //$NON-NLS-1$\n" +
                "        sb.append(directory.getAbsolutePath());\n" +
                "        return sb.toString();\n" +
                "    case FILE:\n" +
                "        // fall through\n" +
                "    case FOLDER:\n" +
                "        return ((File) node.getObject()).getName();\n" +
                "    case BRANCHES:\n" +
                "        return UIText.RepositoriesView_Branches_Nodetext;\n" +
                "    case LOCAL:\n" +
                "        return UIText.RepositoriesViewLabelProvider_LocalNodetext;\n" +
                "    case REMOTETRACKING:\n" +
                "        return UIText.RepositoriesViewLabelProvider_RemoteTrackingNodetext;\n" +
                "    case BRANCHHIERARCHY:\n" +
                "        IPath fullPath = (IPath) node.getObject();\n" +
                "        return fullPath.lastSegment();\n" +
                "    case TAGS:\n" +
                "        return UIText.RepositoriesViewLabelProvider_TagsNodeText;\n" +
                "    case ADDITIONALREFS:\n" +
                "        return UIText.RepositoriesViewLabelProvider_SymbolicRefNodeText;\n" +
                "    case REMOTES:\n" +
                "        return UIText.RepositoriesView_RemotesNodeText;\n" +
                "    case REF:\n" +
                "        // fall through\n" +
                "    case TAG: {\n" +
                "        Ref ref = (Ref) node.getObject();\n" +
                "        // shorten the name\n" +
                "        String refName = Repository.shortenRefName(ref.getName());\n" +
                "        if (node.getParent().getType() == RepositoryTreeNodeType.BRANCHHIERARCHY) {\n" +
                "            int index = refName.lastIndexOf('/');\n" +
                "            refName = refName.substring(index + 1);\n" +
                "        }\n" +
                "        return refName;\n" +
                "    }\n" +
                "    case ADDITIONALREF: {\n" +
                "        Ref ref = (Ref) node.getObject();\n" +
                "        // shorten the name\n" +
                "        String refName = Repository.shortenRefName(ref.getName());\n" +
                "        if (ref.isSymbolic()) {\n" +
                "            refName = refName\n" +
                "                    + \" - \" //$NON-NLS-1$\n" +
                "                    + ref.getLeaf().getName()\n" +
                "                    + \" - \" + ObjectId.toString(ref.getLeaf().getObjectId()); //$NON-NLS-1$\n" +
                "        } else {\n" +
                "            refName = refName + \" - \" //$NON-NLS-1$\n" +
                "                    + ObjectId.toString(ref.getObjectId());\n" +
                "        }\n" +
                "        return refName;\n" +
                "    }\n" +
                "    case WORKINGDIR:\n" +
                "        if (node.getRepository().isBare())\n" +
                "            return UIText.RepositoriesView_WorkingDir_treenode\n" +
                "                    + \" - \" //$NON-NLS-1$\n" +
                "                    + UIText.RepositoriesViewLabelProvider_BareRepositoryMessage;\n" +
                "        else\n" +
                "            return UIText.RepositoriesView_WorkingDir_treenode + \" - \" //$NON-NLS-1$\n" +
                "                    + node.getRepository().getWorkTree().getAbsolutePath();\n" +
                "    case REMOTE:\n" +
                "        // fall through\n" +
                "    case PUSH:\n" +
                "        // fall through\n" +
                "    case FETCH:\n" +
                "        // fall through\n" +
                "    case ERROR:\n" +
                "        return (String) node.getObject();\n" +
                "    }\n" +
                "    return null;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCommentRatio();
        assertEquals(String.format("%.5f",13/(float)74) , String.format("%.5f", sourcecode.commentRatio));
    }
    @Test
    public void testCalcCommentRatio4(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/* (non-Javadoc)\n" +
                " * @see org.eclipse.egit.core.op.IEGitOperation#execute(org.eclipse.core.runtime.IProgressMonitor)\n" +
                " */\n" +
                "public void execute(IProgressMonitor m) throws CoreException {\n" +
                "    IProgressMonitor monitor;\n" +
                "    if (m == null)\n" +
                "        monitor = new NullProgressMonitor();\n" +
                "    else\n" +
                "        monitor = m;\n" +
                "    if (type == ResetType.HARD) {\n" +
                "        IWorkspaceRunnable action = new IWorkspaceRunnable() {\n" +
                "            public void run(IProgressMonitor actMonitor) throws CoreException {\n" +
                "                reset(actMonitor);\n" +
                "            }\n" +
                "        };\n" +
                "        // lock workspace to protect working tree changes\n" +
                "        ResourcesPlugin.getWorkspace().run(action, monitor);\n" +
                "    } else {\n" +
                "        reset(monitor);\n" +
                "    }\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCommentRatio();
        assertEquals(String.format("%.5f",0.19047619) , String.format("%.5f", sourcecode.commentRatio));
    }

    @Test
    public void testCalcCountPath1(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private boolean isEntryIgnoredByTeamProvider(IResource resource) {\n" +
                "    if (resource.getType() == IResource.ROOT\n" +
                "            || resource.getType() == IResource.PROJECT)\n" +
                "        return false;\n" +
                "    if (Team.isIgnoredHint(resource))\n" +
                "        return true;\n" +
                "    return isEntryIgnoredByTeamProvider(resource.getParent());\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCountPath();
        assertEquals(4, sourcecode.countPath);
    }
    @Test
    public void testCalcCountPath2(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private int calculateKindImpl(Repository repo, TreeWalk tw, int srcNth,\n" +
                "        int dstNth) throws IOException {\n" +
                "    ObjectId srcId = tw.getObjectId(srcNth);\n" +
                "    ObjectId dstId = tw.getObjectId(dstNth);\n" +
                "    if (srcId.equals(zeroId()))\n" +
                "        return INCOMING | ADDITION;\n" +
                "    if (dstId.equals(zeroId()))\n" +
                "        return OUTGOING | ADDITION;\n" +
                "    if (!srcId.equals(dstId)) {\n" +
                "        RevWalk rw = new RevWalk(repo);\n" +
                "        RevFlag srcFlag = rw.newFlag(\"source\"); //$NON-NLS-1$\n" +
                "        RevFlag dstFlag = rw.newFlag(\"destination\"); //$NON-NLS-1$\n" +
                "        initializeRevWalk(rw, srcFlag, dstFlag);\n" +
                "        RevCommit commit = rw.next();\n" +
                "        if (commit.has(srcFlag))\n" +
                "            return OUTGOING | CHANGE;\n" +
                "        else if (commit.has(dstFlag))\n" +
                "            return INCOMING | CHANGE;\n" +
                "        else\n" +
                "            return CONFLICTING | CHANGE;\n" +
                "    }\n" +
                "    return IN_SYNC;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCountPath();
        assertEquals(16 , sourcecode.countPath);
    }
    @Test
    public void testCalcCountPath3(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/**\n" +
                " * Recalculates source, destination and ancestor Rev commits\n" +
                " *\n" +
                " * @throws IOException\n" +
                " */\n" +
                "public void updateRevs() throws IOException {\n" +
                "    ObjectWalk ow = new ObjectWalk(repo);\n" +
                "    if (srcRev.length() > 0)\n" +
                "        this.srcRevCommit = ow.parseCommit(repo.resolve(srcRev));\n" +
                "    else\n" +
                "        this.srcRevCommit = null;\n" +
                "    if (dstRev.length() > 0)\n" +
                "        this.dstRevCommit = ow.parseCommit(repo.resolve(dstRev));\n" +
                "    else\n" +
                "        this.dstRevCommit = null;\n" +
                "    if (this.dstRevCommit != null || this.srcRevCommit != null)\n" +
                "        this.ancestorRevCommit = getCommonAncestor(repo, this.srcRevCommit,\n" +
                "                this.dstRevCommit);\n" +
                "    else\n" +
                "        this.ancestorRevCommit = null;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCountPath();
        assertEquals(8 , sourcecode.countPath);
    }
    @Test
    public void testCalcCountPath4(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private Image decorateImage(final Image image, Object element) {\n" +
                "    RepositoryTreeNode node = (RepositoryTreeNode) element;\n" +
                "    switch (node.getType()) {\n" +
                "    case TAG:\n" +
                "        // fall through\n" +
                "    case ADDITIONALREF:\n" +
                "        // fall through\n" +
                "    case REF:\n" +
                "        // if the branch or tag is checked out,\n" +
                "        // we want to decorate the corresponding\n" +
                "        // node with a little check indicator\n" +
                "        String refName = ((Ref) node.getObject()).getName();\n" +
                "        Ref leaf = ((Ref) node.getObject()).getLeaf();\n" +
                "        String branchName;\n" +
                "        String compareString;\n" +
                "        try {\n" +
                "            branchName = node.getRepository().getFullBranch();\n" +
                "            if (branchName == null)\n" +
                "                return image;\n" +
                "            if (refName.startsWith(Constants.R_HEADS)) {\n" +
                "                // local branch: HEAD would be on the branch\n" +
                "                compareString = refName;\n" +
                "            } else if (refName.startsWith(Constants.R_TAGS)) {\n" +
                "                // tag: HEAD would be on the commit id to which the tag is\n" +
                "                // pointing\n" +
                "                ObjectId id = node.getRepository().resolve(refName);\n" +
                "                if (id == null)\n" +
                "                    return image;\n" +
                "                RevWalk rw = new RevWalk(node.getRepository());\n" +
                "                RevTag tag = rw.parseTag(id);\n" +
                "                compareString = tag.getObject().name();\n" +
                "            } else if (refName.startsWith(Constants.R_REMOTES)) {\n" +
                "                // remote branch: HEAD would be on the commit id to which\n" +
                "                // the branch is pointing\n" +
                "                ObjectId id = node.getRepository().resolve(refName);\n" +
                "                if (id == null)\n" +
                "                    return image;\n" +
                "                RevWalk rw = new RevWalk(node.getRepository());\n" +
                "                RevCommit commit = rw.parseCommit(id);\n" +
                "                compareString = commit.getId().name();\n" +
                "            } else if (refName.equals(Constants.HEAD))\n" +
                "                return getDecoratedImage(image);\n" +
                "            else {\n" +
                "                String leafname = leaf.getName();\n" +
                "                if (leafname.startsWith(Constants.R_REFS)\n" +
                "                        && leafname.equals(node.getRepository()\n" +
                "                                .getFullBranch()))\n" +
                "                    return getDecoratedImage(image);\n" +
                "                else if (leaf.getObjectId().equals(\n" +
                "                        node.getRepository().resolve(Constants.HEAD)))\n" +
                "                    return getDecoratedImage(image);\n" +
                "                // some other symbolic reference\n" +
                "                return image;\n" +
                "            }\n" +
                "        } catch (IOException e1) {\n" +
                "            return image;\n" +
                "        }\n" +
                "        if (compareString.equals(branchName)) {\n" +
                "            return getDecoratedImage(image);\n" +
                "        }\n" +
                "        return image;\n" +
                "    default:\n" +
                "        return image;\n" +
                "    }\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCountPath();
        assertEquals(109 , sourcecode.countPath);
    }
    @Test
    public void testCalcCountPath5(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "public Object execute(final ExecutionEvent event) throws ExecutionException {\n" +
                "    final List<File> fileList = new ArrayList<File>();\n" +
                "    Repository repo = null;\n" +
                "    final RepositoryTreeNode nodeToShow;\n" +
                "    List<RepositoryTreeNode> selectedNodes = getSelectedNodes(event);\n" +
                "    if (selectedNodes.size() == 1) {\n" +
                "        RepositoryTreeNode selectedNode = selectedNodes.get(0);\n" +
                "        if (selectedNode.getType() == RepositoryTreeNodeType.REPO\n" +
                "                || selectedNode.getType() == RepositoryTreeNodeType.FILE\n" +
                "                || selectedNode.getType() == RepositoryTreeNodeType.FOLDER)\n" +
                "            nodeToShow = selectedNode;\n" +
                "        else\n" +
                "            nodeToShow = null;\n" +
                "    } else\n" +
                "        nodeToShow = null;\n" +
                "    if (nodeToShow == null)\n" +
                "        for (RepositoryTreeNode node : getSelectedNodes(event)) {\n" +
                "            if (repo == null)\n" +
                "                repo = node.getRepository();\n" +
                "            if (repo != node.getRepository())\n" +
                "                throw new ExecutionException(\n" +
                "                        UIText.AbstractHistoryCommanndHandler_NoUniqueRepository);\n" +
                "            if (node.getType() == RepositoryTreeNodeType.FOLDER) {\n" +
                "                fileList.add(((FolderNode) node).getObject());\n" +
                "            }\n" +
                "            if (node.getType() == RepositoryTreeNodeType.FILE) {\n" +
                "                fileList.add(((FileNode) node).getObject());\n" +
                "            }\n" +
                "        }\n" +
                "    final Repository repoToShow = repo;\n" +
                "    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {\n" +
                "        public void run() {\n" +
                "            try {\n" +
                "                IHistoryView part = (IHistoryView) PlatformUI\n" +
                "                        .getWorkbench().getActiveWorkbenchWindow()\n" +
                "                        .getActivePage().showView(IHistoryView.VIEW_ID);\n" +
                "                if (nodeToShow != null)\n" +
                "                    part.showHistoryFor(nodeToShow);\n" +
                "                else {\n" +
                "                    part.showHistoryFor(new HistoryPageInput(repoToShow,\n" +
                "                            fileList.toArray(new File[fileList.size()])));\n" +
                "                }\n" +
                "            } catch (PartInitException e1) {\n" +
                "                Activator.handleError(e1.getMessage(), e1, true);\n" +
                "            }\n" +
                "        }\n" +
                "    });\n" +
                "    return null;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcCountPath();
        assertEquals(51 , sourcecode.countPath);
    }
    @Test
    public void testCalcComplexity1(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/** Retrieve file status from an already calculated IndexDiff\n" +
                " * @param path\n" +
                " * @param indexDiff\n" +
                " * @return file status\n" +
                " */\n" +
                "private static Status getFileStatus(String path, IndexDiff indexDiff) {\n" +
                "    if (indexDiff.getAssumeUnchanged().contains(path)) {\n" +
                "        return Status.ASSUME_UNCHANGED;\n" +
                "    } else if (indexDiff.getAdded().contains(path)) {\n" +
                "        // added\n" +
                "        if (indexDiff.getModified().contains(path))\n" +
                "            return Status.ADDED_INDEX_DIFF;\n" +
                "        else\n" +
                "            return Status.ADDED;\n" +
                "    } else if (indexDiff.getChanged().contains(path)) {\n" +
                "        // changed\n" +
                "        if (indexDiff.getModified().contains(path))\n" +
                "            return Status.MODIFIED_INDEX_DIFF;\n" +
                "        else\n" +
                "            return Status.MODIFIED;\n" +
                "    } else if (indexDiff.getUntracked().contains(path)) {\n" +
                "        // untracked\n" +
                "        if (indexDiff.getRemoved().contains(path))\n" +
                "            return Status.REMOVED_UNTRACKED;\n" +
                "        else\n" +
                "            return Status.UNTRACKED;\n" +
                "    } else if (indexDiff.getRemoved().contains(path)) {\n" +
                "        // removed\n" +
                "        return Status.REMOVED;\n" +
                "    } else if (indexDiff.getMissing().contains(path)) {\n" +
                "        // missing\n" +
                "        return Status.REMOVED_NOT_STAGED;\n" +
                "    } else if (indexDiff.getModified().contains(path)) {\n" +
                "        // modified (and not changed!)\n" +
                "        return Status.MODIFIED_NOT_STAGED;\n" +
                "    }\n" +
                "    return Status.UNKNOWN;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcComplexity();
        assertEquals(14, sourcecode.complexity);
    }
    @Test
    public void testCalcComplexity2(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/**\n" +
                " * Computer file diffs for specified tree walk and commit\n" +
                " *\n" +
                " * @param walk\n" +
                " * @param commit\n" +
                " * @return non-null but possibly empty array of file diffs\n" +
                " * @throws MissingObjectException\n" +
                " * @throws IncorrectObjectTypeException\n" +
                " * @throws CorruptObjectException\n" +
                " * @throws IOException\n" +
                " */\n" +
                "public static FileDiff[] compute(final TreeWalk walk, final RevCommit commit)\n" +
                "        throws MissingObjectException, IncorrectObjectTypeException,\n" +
                "        CorruptObjectException, IOException {\n" +
                "    final ArrayList<FileDiff> r = new ArrayList<FileDiff>();\n" +
                "    if (commit.getParentCount() > 0)\n" +
                "        walk.reset(trees(commit));\n" +
                "    else {\n" +
                "        walk.reset();\n" +
                "        walk.addTree(new EmptyTreeIterator());\n" +
                "        walk.addTree(commit.getTree());\n" +
                "    }\n" +
                "    if (walk.getTreeCount() <= 2) {\n" +
                "        List<DiffEntry> entries = DiffEntry.scan(walk);\n" +
                "        for (DiffEntry entry : entries) {\n" +
                "            final FileDiff d = new FileDiff(commit, entry);\n" +
                "            r.add(d);\n" +
                "        }\n" +
                "    }\n" +
                "    else { // DiffEntry does not support walks with more than two trees\n" +
                "        final int nTree = walk.getTreeCount();\n" +
                "        final int myTree = nTree - 1;\n" +
                "        while (walk.next()) {\n" +
                "            if (matchAnyParent(walk, myTree))\n" +
                "                continue;\n" +
                "            final FileDiffForMerges d = new FileDiffForMerges(commit);\n" +
                "            d.path = walk.getPathString();\n" +
                "            int m0 = 0;\n" +
                "            for (int i = 0; i < myTree; i++)\n" +
                "                m0 |= walk.getRawMode(i);\n" +
                "            final int m1 = walk.getRawMode(myTree);\n" +
                "            d.change = ChangeType.MODIFY;\n" +
                "            if (m0 == 0 && m1 != 0)\n" +
                "                d.change = ChangeType.ADD;\n" +
                "            else if (m0 != 0 && m1 == 0)\n" +
                "                d.change = ChangeType.DELETE;\n" +
                "            else if (m0 != m1 && walk.idEqual(0, myTree))\n" +
                "                d.change = ChangeType.MODIFY; // there is no ChangeType.TypeChanged\n" +
                "            d.blobs = new ObjectId[nTree];\n" +
                "            d.modes = new FileMode[nTree];\n" +
                "            for (int i = 0; i < nTree; i++) {\n" +
                "                d.blobs[i] = walk.getObjectId(i);\n" +
                "                d.modes[i] = walk.getFileMode(i);\n" +
                "            }\n" +
                "            r.add(d);\n" +
                "        }\n" +
                "    }\n" +
                "    final FileDiff[] tmp = new FileDiff[r.size()];\n" +
                "    r.toArray(tmp);\n" +
                "    return tmp;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcComplexity();
        assertEquals(13, sourcecode.complexity);
    }
    @Test
    public void testCalcComplexity3(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/**\n" +
                " * Enable the command if all of the following conditions are fulfilled: <li>\n" +
                " * All selected nodes belong to the same repository <li>All selected nodes\n" +
                " * are of type FileNode or FolderNode or WorkingTreeNode <li>Each node does\n" +
                " * not represent a file / folder in the git directory\n" +
                " *\n" +
                " * @param evaluationContext\n" +
                " */\n" +
                "protected void enableWorkingDirCommand(Object evaluationContext) {\n" +
                "    if (!(evaluationContext instanceof EvaluationContext)) {\n" +
                "        setBaseEnabled(false);\n" +
                "        return;\n" +
                "    }\n" +
                "    EvaluationContext context = (EvaluationContext) evaluationContext;\n" +
                "    Object selection = context\n" +
                "            .getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);\n" +
                "    if (!(selection instanceof TreeSelection)) {\n" +
                "        setBaseEnabled(false);\n" +
                "        return;\n" +
                "    }\n" +
                "    Repository repository = null;\n" +
                "    TreeSelection treeSelection = (TreeSelection) selection;\n" +
                "    for (Iterator iterator = treeSelection.iterator(); iterator.hasNext();) {\n" +
                "        Object object = iterator.next();\n" +
                "        if (!(object instanceof RepositoryTreeNode)) {\n" +
                "            setBaseEnabled(false);\n" +
                "            return;\n" +
                "        }\n" +
                "        Repository nodeRepository = ((RepositoryTreeNode) object)\n" +
                "                .getRepository();\n" +
                "        if (repository == null)\n" +
                "            repository = nodeRepository;\n" +
                "        else if (repository != nodeRepository) {\n" +
                "            setBaseEnabled(false);\n" +
                "            return;\n" +
                "        }\n" +
                "        if (!(object instanceof WorkingDirNode)) {\n" +
                "            String path;\n" +
                "            if (object instanceof FolderNode) {\n" +
                "                path = ((FolderNode) object).getObject().getAbsolutePath();\n" +
                "            } else {\n" +
                "                if (object instanceof FileNode) {\n" +
                "                    path = ((FileNode) object).getObject()\n" +
                "                            .getAbsolutePath();\n" +
                "                } else {\n" +
                "                    setBaseEnabled(false);\n" +
                "                    return;\n" +
                "                }\n" +
                "            }\n" +
                "            if (path.startsWith(repository.getDirectory().getAbsolutePath())) {\n" +
                "                setBaseEnabled(false);\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    setBaseEnabled(true);\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcComplexity();
        assertEquals( 13, sourcecode.complexity);
    }
    @Test
    public void testCalcComplexity4(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private IStructuredSelection convertSelection(IEvaluationContext aContext,\n" +
                "        Object aSelection) {\n" +
                "    IEvaluationContext ctx;\n" +
                "    if (aContext == null && aSelection == null)\n" +
                "        return StructuredSelection.EMPTY;\n" +
                "    else\n" +
                "        ctx = aContext;\n" +
                "    Object selection;\n" +
                "    if (aSelection == null && ctx != null) {\n" +
                "        selection = ctx.getVariable(ISources.ACTIVE_MENU_SELECTION_NAME);\n" +
                "        if (selection == null)\n" +
                "            selection = ctx\n" +
                "                    .getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);\n" +
                "    } else if (aSelection != null)\n" +
                "        selection = aSelection;\n" +
                "    else\n" +
                "        return StructuredSelection.EMPTY;\n" +
                "    if (selection instanceof TextSelection) {\n" +
                "        if (ctx == null)\n" +
                "            ctx = getEvaluationContext();\n" +
                "        IResource resource = ResourceUtil.getResource(ctx\n" +
                "                .getVariable(ISources.ACTIVE_EDITOR_INPUT_NAME));\n" +
                "        if (resource != null)\n" +
                "            return new StructuredSelection(resource);\n" +
                "    }\n" +
                "    if (selection instanceof IStructuredSelection)\n" +
                "        return (IStructuredSelection) selection;\n" +
                "    return StructuredSelection.EMPTY;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcComplexity();
        assertEquals(11 , sourcecode.complexity);
    }
    @Test
    public void testCalcComplexity5(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private boolean shouldRecurse(TreeWalk treeWalk) throws IOException {\n" +
                "    final WorkingTreeIterator workspaceIterator = treeWalk.getTree(\n" +
                "            DecoratableResourceHelper.T_WORKSPACE,\n" +
                "            WorkingTreeIterator.class);\n" +
                "    if (workspaceIterator instanceof AdaptableFileTreeIterator)\n" +
                "        return true;\n" +
                "    ResourceEntry resourceEntry = null;\n" +
                "    if (workspaceIterator != null)\n" +
                "        resourceEntry = ((ContainerTreeIterator) workspaceIterator)\n" +
                "                .getResourceEntry();\n" +
                "    if (resourceEntry == null)\n" +
                "        return true;\n" +
                "    IResource visitingResource = resourceEntry.getResource();\n" +
                "    if (targetDepth == -1) {\n" +
                "        if (visitingResource.equals(resource)\n" +
                "                || visitingResource.getParent().equals(resource))\n" +
                "            targetDepth = treeWalk.getDepth();\n" +
                "        else\n" +
                "            return true;\n" +
                "    }\n" +
                "    if ((treeWalk.getDepth() - targetDepth) >= recurseLimit) {\n" +
                "        if (visitingResource.equals(resource))\n" +
                "            extractResourceProperties(treeWalk);\n" +
                "        return false;\n" +
                "    }\n" +
                "    return true;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcComplexity();
        assertEquals(9 , sourcecode.complexity);
    }
    @Test
    public void testCalcExecStmt1(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/**\n" +
                " * Retrieves a collection of files that may be committed based on the user's\n" +
                " * selection when they performed the commit action. That is, even if the\n" +
                " * user only selected one folder when the action was performed, if the\n" +
                " * folder contains any files that could be committed, they will be returned.\n" +
                " *\n" +
                " * @return a collection of files that is eligible to be committed based on\n" +
                " *         the user's selection\n" +
                " */\n" +
                "private Set<String> getSelectedFiles() {\n" +
                "    Set<String> preselectionCandidates = new LinkedHashSet<String>();\n" +
                "    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();\n" +
                "    // iterate through all the files that may be committed\n" +
                "    for (String fileName : files) {\n" +
                "        URI uri = new File(repo.getWorkTree(), fileName).toURI();\n" +
                "        IFile[] workspaceFiles = root.findFilesForLocationURI(uri);\n" +
                "        if (workspaceFiles.length > 0) {\n" +
                "            IFile file = workspaceFiles[0];\n" +
                "            for (IResource resource : selectedResources) {\n" +
                "                // if any selected resource contains the file, add it as a\n" +
                "                // preselection candidate\n" +
                "                if (resource.contains(file)) {\n" +
                "                    preselectionCandidates.add(fileName);\n" +
                "                    break;\n" +
                "                }\n" +
                "            }\n" +
                "        } else {\n" +
                "            // could be file outside of workspace\n" +
                "            for (IResource resource : selectedResources) {\n" +
                "                if(resource.getFullPath().toFile().equals(new File(uri))) {\n" +
                "                    preselectionCandidates.add(fileName);\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    return preselectionCandidates;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcExecStmt();
        assertEquals(10 , sourcecode.execStmt);
    }
    @Test
    public void testCalcExecStmt2(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/**\n" +
                " * @param element\n" +
                " * @param adapterType\n" +
                " * @param load\n" +
                " * @return the adapted element, or null\n" +
                " */\n" +
                "private static Object getAdapter(Object element, Class adapterType,\n" +
                "        boolean load) {\n" +
                "    if (adapterType.isInstance(element))\n" +
                "        return element;\n" +
                "    if (element instanceof IAdaptable) {\n" +
                "        Object adapted = ((IAdaptable) element).getAdapter(adapterType);\n" +
                "        if (adapterType.isInstance(adapted))\n" +
                "            return adapted;\n" +
                "    }\n" +
                "    if (load) {\n" +
                "        Object adapted = Platform.getAdapterManager().loadAdapter(element,\n" +
                "                adapterType.getName());\n" +
                "        if (adapterType.isInstance(adapted))\n" +
                "            return adapted;\n" +
                "    } else {\n" +
                "        Object adapted = Platform.getAdapterManager().getAdapter(element,\n" +
                "                adapterType);\n" +
                "        if (adapterType.isInstance(adapted))\n" +
                "            return adapted;\n" +
                "    }\n" +
                "    return null;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcExecStmt();
        assertEquals(11 , sourcecode.execStmt);
    }
    @Test
    public void testCalcExecStmt3(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "public Object execute(ExecutionEvent event) throws ExecutionException {\n" +
                "    // assert all resources map to the same repository\n" +
                "    if (getRepository(true, event) == null)\n" +
                "        return null;\n" +
                "    final IResource[] resources = getSelectedResources(event);\n" +
                "    if (resources.length == 1 && resources[0] instanceof IFile) {\n" +
                "        final IFile baseFile = (IFile) resources[0];\n" +
                "        final ITypedElement base = SaveableCompareEditorInput\n" +
                "                .createFileElement(baseFile);\n" +
                "        final ITypedElement next;\n" +
                "        try {\n" +
                "            next = getHeadTypedElement(baseFile);\n" +
                "        } catch (IOException e) {\n" +
                "            Activator.handleError(\n" +
                "                    UIText.CompareWithIndexAction_errorOnAddToIndex, e,\n" +
                "                    true);\n" +
                "            return null;\n" +
                "        }\n" +
                "        final GitCompareFileRevisionEditorInput in = new GitCompareFileRevisionEditorInput(\n" +
                "                base, next, null);\n" +
                "        CompareUI.openCompareEditor(in);\n" +
                "    } else {\n" +
                "        CompareTreeView view;\n" +
                "        try {\n" +
                "            view = (CompareTreeView) PlatformUI.getWorkbench()\n" +
                "                    .getActiveWorkbenchWindow().getActivePage().showView(\n" +
                "                            CompareTreeView.ID);\n" +
                "            view.setInput(resources, CompareTreeView.INDEX_VERSION);\n" +
                "        } catch (PartInitException e) {\n" +
                "            Activator.handleError(e.getMessage(), e, true);\n" +
                "        }\n" +
                "    }\n" +
                "    return null;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcExecStmt();
        assertEquals(13 , sourcecode.execStmt);
    }

    @Test
    public void testCalcMaxNesting1(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata =
                "private Image decorateImage(final Image image, Object element) {\n" +
                "    RepositoryTreeNode node = (RepositoryTreeNode) element;\n" +
                "    switch (node.getType()) {\n" +
                "    case TAG:\n" +
                "        // fall through\n" +
                "    case ADDITIONALREF:\n" +
                "        // fall through\n" +
                "    case REF:\n" +
                "        // if the branch or tag is checked out,\n" +
                "        // we want to decorate the corresponding\n" +
                "        // node with a little check indicator\n" +
                "        String refName = ((Ref) node.getObject()).getName();\n" +
                "        Ref leaf = ((Ref) node.getObject()).getLeaf();\n" +
                "        String branchName;\n" +
                "        String compareString;\n" +
                "        try {\n" +
                "            branchName = node.getRepository().getFullBranch();\n" +
                "            if (branchName == null)\n" +
                "                return image;\n" +
                "            if (refName.startsWith(Constants.R_HEADS)) {\n" +
                "                // local branch: HEAD would be on the branch\n" +
                "                compareString = refName;\n" +
                "            } else if (refName.startsWith(Constants.R_TAGS)) {\n" +
                "                // tag: HEAD would be on the commit id to which the tag is\n" +
                "                // pointing\n" +
                "                ObjectId id = node.getRepository().resolve(refName);\n" +
                "                if (id == null)\n" +
                "                    return image;\n" +
                "                RevWalk rw = new RevWalk(node.getRepository());\n" +
                "                RevTag tag = rw.parseTag(id);\n" +
                "                compareString = tag.getObject().name();\n" +
                "            } else if (refName.startsWith(Constants.R_REMOTES)) {\n" +
                "                // remote branch: HEAD would be on the commit id to which\n" +
                "                // the branch is pointing\n" +
                "                ObjectId id = node.getRepository().resolve(refName);\n" +
                "                if (id == null)\n" +
                "                    return image;\n" +
                "                RevWalk rw = new RevWalk(node.getRepository());\n" +
                "                RevCommit commit = rw.parseCommit(id);\n" +
                "                compareString = commit.getId().name();\n" +
                "            } else if (refName.equals(Constants.HEAD))\n" +
                "                return getDecoratedImage(image);\n" +
                "            else {\n" +
                "                String leafname = leaf.getName();\n" +
                "                if (leafname.startsWith(Constants.R_REFS)\n" +
                "                        && leafname.equals(node.getRepository()\n" +
                "                                .getFullBranch()))\n" +
                "                    return getDecoratedImage(image);\n" +
                "                else if (leaf.getObjectId().equals(\n" +
                "                        node.getRepository().resolve(Constants.HEAD)))\n" +
                "                    return getDecoratedImage(image);\n" +
                "                // some other symbolic reference\n" +
                "                return image;\n" +
                "            }\n" +
                "        } catch (IOException e1) {\n" +
                "            return image;\n" +
                "        }\n" +
                "        if (compareString.equals(branchName)) {\n" +
                "            return getDecoratedImage(image);\n" +
                "        }\n" +
                "        return image;\n" +
                "    default:\n" +
                "        return image;\n" +
                "    }\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcMaxNesting();
        assertEquals(8 , sourcecode.maxNesting);
    }
    @Test
    public void testCalcMaxNesting2(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "public void createControl(Composite parent) {\n" +
                "    Composite main = new Composite(parent, SWT.NO_RADIO_GROUP);\n" +
                "    main.setLayout(new GridLayout(1, false));\n" +
                "    SelectionListener sl = new SelectionAdapter() {\n" +
                "        @Override\n" +
                "        public void widgetSelected(SelectionEvent e) {\n" +
                "            tv.getTree().setEnabled(!newProjectWizard.getSelection());\n" +
                "            if (importExisting.getSelection())\n" +
                "                wizardSelection = EXISTING_PROJECTS_WIZARD;\n" +
                "            else if (newProjectWizard.getSelection())\n" +
                "                wizardSelection = NEW_WIZARD;\n" +
                "            else if (generalWizard.getSelection())\n" +
                "                wizardSelection = GENERAL_WIZARD;\n" +
                "            else\n" +
                "                wizardSelection = EXISTING_PROJECTS_WIZARD;\n" +
                "            checkPage();\n" +
                "        }\n" +
                "    };\n" +
                "    Group wizardType = new Group(main, SWT.SHADOW_ETCHED_IN);\n" +
                "    GridDataFactory.fillDefaults().grab(true, false).applyTo(wizardType);\n" +
                "    wizardType.setText(UIText.GitSelectWizardPage_ProjectCreationHeader);\n" +
                "    wizardType.setLayout(new GridLayout(1, false));\n" +
                "    importExisting = new Button(wizardType, SWT.RADIO);\n" +
                "    importExisting.setText(UIText.GitSelectWizardPage_ImportExistingButton);\n" +
                "    importExisting.addSelectionListener(sl);\n" +
                "    newProjectWizard = new Button(wizardType, SWT.RADIO);\n" +
                "    newProjectWizard\n" +
                "            .setText(UIText.GitSelectWizardPage_UseNewProjectsWizardButton);\n" +
                "    newProjectWizard.addSelectionListener(sl);\n" +
                "    generalWizard = new Button(wizardType, SWT.RADIO);\n" +
                "    generalWizard.setText(UIText.GitSelectWizardPage_ImportAsGeneralButton);\n" +
                "    generalWizard.addSelectionListener(sl);\n" +
                "    IDialogSettings settings = Activator.getDefault().getDialogSettings();\n" +
                "    try {\n" +
                "        wizardSelection = settings.getInt(PREF_WIZ);\n" +
                "    } catch (NumberFormatException e) {\n" +
                "        wizardSelection = EXISTING_PROJECTS_WIZARD;\n" +
                "    }\n" +
                "    switch (wizardSelection) {\n" +
                "    case EXISTING_PROJECTS_WIZARD:\n" +
                "        importExisting.setSelection(true);\n" +
                "        break;\n" +
                "    case GENERAL_WIZARD:\n" +
                "        generalWizard.setSelection(true);\n" +
                "        break;\n" +
                "    case NEW_WIZARD:\n" +
                "        newProjectWizard.setSelection(true);\n" +
                "        break;\n" +
                "    }\n" +
                "    tv = new TreeViewer(main, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL\n" +
                "            | SWT.BORDER);\n" +
                "    RepositoriesViewContentProvider cp = new RepositoriesViewContentProvider();\n" +
                "    tv.setContentProvider(cp);\n" +
                "    GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200)\n" +
                "            .applyTo(tv.getTree());\n" +
                "    tv.setLabelProvider(new RepositoriesViewLabelProvider());\n" +
                "    tv.addSelectionChangedListener(new ISelectionChangedListener() {\n" +
                "        public void selectionChanged(SelectionChangedEvent event) {\n" +
                "            checkPage();\n" +
                "        }\n" +
                "    });\n" +
                "    if (initialRepository != null) {\n" +
                "        List<WorkingDirNode> input = new ArrayList<WorkingDirNode>();\n" +
                "        WorkingDirNode node = new WorkingDirNode(null, initialRepository);\n" +
                "        input.add(node);\n" +
                "        tv.setInput(input);\n" +
                "        // select the working directory as default\n" +
                "        if (initialPath == null)\n" +
                "            tv.setSelection(new StructuredSelection(input.get(0)));\n" +
                "        else {\n" +
                "            RepositoryTreeNode parentNode = node;\n" +
                "            IPath fullPath = new Path(initialPath);\n" +
                "            IPath workdirPath = new Path(initialRepository.getWorkTree()\n" +
                "                    .getPath());\n" +
                "            if (workdirPath.isPrefixOf(fullPath)) {\n" +
                "                IPath relPath = fullPath.removeFirstSegments(workdirPath\n" +
                "                        .segmentCount());\n" +
                "                for (String segment : relPath.segments()) {\n" +
                "                    for (Object child : cp.getChildren(parentNode)) {\n" +
                "                        if (child instanceof FolderNode) {\n" +
                "                            FolderNode childFolder = (FolderNode) child;\n" +
                "                            if (childFolder.getObject().getName().equals(\n" +
                "                                    segment)) {\n" +
                "                                parentNode = childFolder;\n" +
                "                                break;\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "                tv.setSelection(new StructuredSelection(parentNode));\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    tv.getTree().setEnabled(!newProjectWizard.getSelection());\n" +
                "    Dialog.applyDialogFont(main);\n" +
                "    setControl(main);\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcMaxNesting();
        assertEquals(7 , sourcecode.maxNesting);
    }
    @Test
    public void testCalcMaxNesting3(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "/**\n" +
                " * @return The GIT DIR absolute path\n" +
                " */\n" +
                "public synchronized IPath getGitDirAbsolutePath() {\n" +
                "    if (gitDirAbsolutePath == null)\n" +
                "        gitDirAbsolutePath = container.getLocation()\n" +
                "                .append(getGitDirPath());\n" +
                "    return gitDirAbsolutePath;\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcMaxNesting();
        assertEquals(1 , sourcecode.maxNesting);
    }
    @Test
    public void testCalcMaxNesting4(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "public boolean performFinish() {\n" +
                "    if (!existingPage.getInternalMode()) {\n" +
                "        try {\n" +
                "            final Map<IProject, File> projectsToMove = existingPage\n" +
                "                    .getProjects(true);\n" +
                "            final Repository selectedRepository = existingPage\n" +
                "                    .getSelectedRepsoitory();\n" +
                "            getContainer().run(false, false, new IRunnableWithProgress() {\n" +
                "                public void run(IProgressMonitor monitor)\n" +
                "                        throws InvocationTargetException,\n" +
                "                        InterruptedException {\n" +
                "                    for (Map.Entry<IProject, File> entry : projectsToMove\n" +
                "                            .entrySet()) {\n" +
                "                        IPath targetLocation = new Path(entry.getValue()\n" +
                "                                .getPath());\n" +
                "                        IPath currentLocation = entry.getKey()\n" +
                "                                .getLocation();\n" +
                "                        if (!targetLocation.equals(currentLocation)) {\n" +
                "                            MoveProjectOperation op = new MoveProjectOperation(\n" +
                "                                    entry.getKey(),\n" +
                "                                    entry.getValue().toURI(),\n" +
                "                                    UIText.SharingWizard_MoveProjectActionLabel);\n" +
                "                            try {\n" +
                "                                IStatus result = op.execute(monitor, null);\n" +
                "                                if (!result.isOK())\n" +
                "                                    throw new RuntimeException();\n" +
                "                            } catch (ExecutionException e) {\n" +
                "                                if (e.getCause() != null)\n" +
                "                                    throw new InvocationTargetException(e\n" +
                "                                            .getCause());\n" +
                "                                throw new InvocationTargetException(e);\n" +
                "                            }\n" +
                "                        }\n" +
                "                        try {\n" +
                "                            new ConnectProviderOperation(entry.getKey(),\n" +
                "                                    selectedRepository.getDirectory())\n" +
                "                                    .execute(monitor);\n" +
                "                        } catch (CoreException e) {\n" +
                "                            throw new InvocationTargetException(e);\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            });\n" +
                "        } catch (InvocationTargetException e) {\n" +
                "            Activator.handleError(UIText.SharingWizard_failed,\n" +
                "                    e.getCause(), true);\n" +
                "            return false;\n" +
                "        } catch (InterruptedException e) {\n" +
                "            // ignore for the moment\n" +
                "        }\n" +
                "        return true;\n" +
                "    } else {\n" +
                "        final ConnectProviderOperation op = new ConnectProviderOperation(\n" +
                "                existingPage.getProjects(true));\n" +
                "        try {\n" +
                "            getContainer().run(true, false, new IRunnableWithProgress() {\n" +
                "                public void run(final IProgressMonitor monitor)\n" +
                "                        throws InvocationTargetException {\n" +
                "                    try {\n" +
                "                        op.execute(monitor);\n" +
                "                        PlatformUI.getWorkbench().getDisplay()\n" +
                "                                .syncExec(new Runnable() {\n" +
                "                                    public void run() {\n" +
                "                                        Set<File> filesToAdd = new HashSet<File>();\n" +
                "                                        // collect all files first\n" +
                "                                        for (Entry<IProject, File> entry : existingPage\n" +
                "                                                .getProjects(true)\n" +
                "                                                .entrySet())\n" +
                "                                            filesToAdd.add(entry.getValue());\n" +
                "                                        // add the files to the repository\n" +
                "                                        // view\n" +
                "                                        for (File file : filesToAdd)\n" +
                "                                            Activator\n" +
                "                                                    .getDefault()\n" +
                "                                                    .getRepositoryUtil()\n" +
                "                                                    .addConfiguredRepository(\n" +
                "                                                            file);\n" +
                "                                    }\n" +
                "                                });\n" +
                "                    } catch (CoreException ce) {\n" +
                "                        throw new InvocationTargetException(ce);\n" +
                "                    }\n" +
                "                }\n" +
                "            });\n" +
                "            return true;\n" +
                "        } catch (Throwable e) {\n" +
                "            if (e instanceof InvocationTargetException) {\n" +
                "                e = e.getCause();\n" +
                "            }\n" +
                "            if (e instanceof CoreException) {\n" +
                "                IStatus status = ((CoreException) e).getStatus();\n" +
                "                e = status.getException();\n" +
                "            }\n" +
                "            Activator.handleError(UIText.SharingWizard_failed, e, true);\n" +
                "            return false;\n" +
                "        }\n" +
                "    }\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcMaxNesting();
        assertEquals(3 , sourcecode.maxNesting);
    }
    @Test
    public void testCalcMaxNesting5(){
        Sourcecode sourcecode = new Sourcecode();
        sourcecode.rawdata = "private void checkPage() {\n" +
                "    String projectName = projectText.getText();\n" +
                "    setErrorMessage(null);\n" +
                "    try {\n" +
                "        // make sure the directory exists\n" +
                "        if (!myDirectory.exists()) {\n" +
                "            setErrorMessage(NLS.bind(\n" +
                "                    UIText.GitCreateGeneralProjectPage_DirNotExistMessage,\n" +
                "                    myDirectory.getPath()));\n" +
                "            return;\n" +
                "        }\n" +
                "        // make sure we don't have a file\n" +
                "        if (!myDirectory.isDirectory()) {\n" +
                "            setErrorMessage(NLS.bind(\n" +
                "                    UIText.GitCreateGeneralProjectPage_FileNotDirMessage,\n" +
                "                    myDirectory.getPath()));\n" +
                "            return;\n" +
                "        }\n" +
                "        // make sure there is not already a .project file\n" +
                "        if (myDirectory.list(new FilenameFilter() {\n" +
                "            public boolean accept(File dir, String name) {\n" +
                "                if (name.equals(\".project\")) //$NON-NLS-1$\n" +
                "                    return true;\n" +
                "                return false;\n" +
                "            }\n" +
                "        }).length > 0) {\n" +
                "            setErrorMessage(NLS\n" +
                "                    .bind(\n" +
                "                            UIText.GitCreateGeneralProjectPage_FileExistsInDirMessage,\n" +
                "                            \".project\", myDirectory.getPath())); //$NON-NLS-1$\n" +
                "            return;\n" +
                "        }\n" +
                "        // project name empty\n" +
                "        if (projectName.length() == 0) {\n" +
                "            setErrorMessage(UIText.GitCreateGeneralProjectPage_EnterProjectNameMessage);\n" +
                "            return;\n" +
                "        }\n" +
                "        // project name valid (no strange chars...)\n" +
                "        IStatus result = ResourcesPlugin.getWorkspace().validateName(\n" +
                "                projectName, IResource.PROJECT);\n" +
                "        if (!result.isOK()) {\n" +
                "            setErrorMessage(result.getMessage());\n" +
                "            return;\n" +
                "        }\n" +
                "        // project already exists\n" +
                "        if (isProjectInWorkspace(projectName)) {\n" +
                "            setErrorMessage(NLS\n" +
                "                    .bind(\n" +
                "                            UIText.GitCreateGeneralProjectPage_PorjectAlreadyExistsMessage,\n" +
                "                            projectName));\n" +
                "            return;\n" +
                "        }\n" +
                "        if(!defaultLocation) {\n" +
                "            IProject newProject = ResourcesPlugin.getWorkspace().getRoot()\n" +
                "                    .getProject(projectName);\n" +
                "            IStatus locationResult = ResourcesPlugin.getWorkspace()\n" +
                "                    .validateProjectLocation(newProject,\n" +
                "                            new Path(myDirectory.getPath()));\n" +
                "            if (!locationResult.isOK()) {\n" +
                "                setErrorMessage(locationResult.getMessage());\n" +
                "                return;\n" +
                "            }\n" +
                "        }\n" +
                "    } finally {\n" +
                "        setPageComplete(getErrorMessage() == null);\n" +
                "    }\n" +
                "}";
        sourcecode.calcCompilationUnit();
        sourcecode.calcMaxNesting();
        assertEquals(3 , sourcecode.maxNesting);
    }
}
