package com.ferramenta.ferramentaAuditoria.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ChecklistData {
    private static final String ARQUIVO = "checklists.dat";
    private static List<Checklist> checklists = new ArrayList<>();

    public static List<Checklist> getChecklists() {
        return checklists;
    }

    public static void adicionarChecklist(Checklist checklist) {
        checklists.add(checklist);
        salvar();
    }

    @SuppressWarnings("unchecked")
    public static void carregar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO))) {
            checklists = (List<Checklist>) ois.readObject();
        } catch (Exception e) {
            checklists = new ArrayList<>();
        }
    }

    public static void salvar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(checklists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
