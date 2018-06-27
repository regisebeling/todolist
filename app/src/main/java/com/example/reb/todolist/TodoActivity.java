package com.example.reb.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import static java.lang.Boolean.TRUE;

public class TodoActivity extends AppCompatActivity implements OnClickListener, OnItemClickListener, TabHost.OnTabChangeListener{

    //Lista de objetos Task
    private List<Task> tasks = new ArrayList<Task>();
    private TaskAdapter adaptadorToDo = null;
    private TaskAdapter adaptadorDone = null;
    private TaskAdapter adaptadorTasks = null;

    // Armazena o conteúdo de uma consulta ao banco de dados
    Cursor cursorToDo;
    Cursor cursorDone;
    Cursor cursorTasks;

    DBTask dbtask;

    //Declarações dos campos views
    private TabHost abas = null;
    private EditText title = null;
    private EditText description = null;
    private EditText dateLimit = null;
    private TextView idTask = null;
    private Button salvar = null;
    private EditText date = null;
    private CheckBox checkDone = null;
    private CheckBox checkDoneEdit = null;



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Tab", abas.getCurrentTab());
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int aba = savedInstanceState.getInt("Tab");
        abas.setCurrentTab(aba);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        dbtask = new DBTask(this);

        //Carrega o Layout de abas
        abas = (TabHost) findViewById(R.id.tabhost);
        abas.setup();

        TabSpec descritor = abas.newTabSpec("aba1"); //Cria novo objeto aba do tipo TabSpec
        descritor.setContent(R.id.todo); //Linka com um bloco da activity
        descritor.setIndicator(getString(R.string.todo)); //Seta o título da aba
        abas.addTab(descritor); //Adiciona as abas

        descritor = abas.newTabSpec("aba2");
        descritor.setContent(R.id.done);
        descritor.setIndicator(getString(R.string.done));
        abas.addTab(descritor); //Adiciona as abas

        descritor = abas.newTabSpec("aba3");
        descritor.setContent(R.id.tasks);
        descritor.setIndicator(getString(R.string.tasks));
        abas.addTab(descritor); //Adiciona as abas

        descritor = abas.newTabSpec("aba4");
        descritor.setContent(R.id.add);
        descritor.setIndicator(getString(R.string.add));
        abas.addTab(descritor);
        //Fim carregamento layout abas

        //Linka os views criados com os views na Activity
        salvar = (Button) findViewById(R.id.salvar);
        salvar.setOnClickListener(this);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        dateLimit = (EditText) findViewById(R.id.dateLimit);

        idTask = (TextView) findViewById(R.id.idTask);

        checkDone = (CheckBox) findViewById(R.id.checkDone);
        checkDoneEdit = (CheckBox) findViewById(R.id.checkDoneEdit);



        ListView listaToDo = (ListView) findViewById(R.id.todo);
        cursorToDo = dbtask.getToDo();
        startManagingCursor(cursorToDo);
        adaptadorToDo = new TaskAdapter(cursorToDo);
        listaToDo.setAdapter(adaptadorToDo);

        ListView listaDone = (ListView) findViewById(R.id.done);
        cursorDone = dbtask.getDone();
        startManagingCursor(cursorDone);
        adaptadorDone = new TaskAdapter(cursorDone);
        listaDone.setAdapter(adaptadorDone);

        ListView listaTasks = (ListView) findViewById(R.id.tasks);
        cursorTasks = dbtask.getTasks();
        startManagingCursor(cursorTasks);
        adaptadorTasks = new TaskAdapter(cursorTasks);
        listaTasks.setAdapter(adaptadorTasks);


        CheckBox checkDone = (CheckBox) findViewById(R.id.checkDone);


        //Listener: ouve o que acontece no backend para repassar a mudança ao frontend
        //Seta a classe como listener da lista
        listaToDo.setOnItemClickListener(this);
        listaDone.setOnItemClickListener(this);
        listaTasks.setOnItemClickListener(this);

        abas.setOnTabChangedListener(this);

    }

    public boolean dateFormat(String dateToCheck){

        //Formato da data "dd/MM/yyyy"

        //Checa se contém / nos lugares certos
        if((dateToCheck.length() != 10) || !(dateToCheck.charAt(2) == '/') || !(dateToCheck.charAt(5) == '/'))
            return false;

        boolean checkNumber;
        int date;
        try {
            date = (Integer.parseInt(dateToCheck.replaceAll("/","")));
            checkNumber = true;
        } catch (NumberFormatException e) {
            checkNumber = false;
        }

        return checkNumber;
    }



    //Pega os dados fornecidos para o novo task e salva, após muda de aba e limpa o formulário
    @Override
    public void onClick(View v) {

        if(dateFormat(dateLimit.getText().toString())) {
            int done;
            String dataDone = "0";

            if (checkDoneEdit.isChecked() == TRUE)
                done = 1;
            else
                done = 0;


            //Escolhe a ação conforme o Id for informado
            if (idTask.getText().toString().equals("")) //Adiciona no Banco o registro
                dbtask.insert(0, title.getText().toString(), description.getText().toString(), dateLimit.getText().toString(), dataDone, done);
            else //Update no registro
                dbtask.updateTask(Integer.valueOf(idTask.getText().toString()), title.getText().toString(), description.getText().toString(), done, dateLimit.getText().toString());

            cursorToDo.requery();
            cursorDone.requery();
            cursorTasks.requery();


            abas.setCurrentTab(0);

            limparFormulario();
        }
    }

    //Função auxiliar para limpar o formulário de novo task
    private void limparFormulario() {
        title.setText("");
        description.setText("");
        dateLimit.setText("");
        checkDoneEdit.setChecked(false);

    }

    @Override
    public void onTabChanged(String s) {
        if(abas.getCurrentTab() == 0 || abas.getCurrentTab() == 1 || abas.getCurrentTab() == 2)
            limparFormulario();

    }


    //Classe TaskAdapter para adaptar uma lista de Tasks no layout
    class TaskAdapter extends CursorAdapter {

        public TaskAdapter(Cursor c) {
            super(TodoActivity.this, c);
        }


        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ArmazenadorDeTasks armazenador = (ArmazenadorDeTasks) view.getTag();
            armazenador.popularTasks(view, cursor, dbtask);

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View linha = inflater.inflate(R.layout.elemento_lista, parent, false);
            ArmazenadorDeTasks armazenador = new ArmazenadorDeTasks(linha);
            linha.setTag(armazenador);
            return linha;
        }

    }

    // Classe que armazena os tasks
    static class ArmazenadorDeTasks {
        private TextView title = null;
        private TextView description = null;
        private CheckBox checkDone = null;
        private TextView date = null;




        public ArmazenadorDeTasks(View linha) {
            title = (TextView) linha.findViewById(R.id.titulo);
            description = (TextView) linha.findViewById(R.id.subtitulo);
            checkDone = (CheckBox) linha.findViewById(R.id.checkDone);
            date = (TextView) linha.findViewById(R.id.date);


        }

        //Carrega cada Task em uma linha
        void popularTasks(View view, Cursor c, DBTask dbtask) {
            title.setText(dbtask.getTitle(c));
            description.setText(dbtask.getDescription(c));
            //date.setText(dbtask.getDateLimit(c));

            ContentValues values = new ContentValues();
            String dateAt = DateFormat.getDateInstance().format(new Date());

            if(((Integer.valueOf(dateAt.substring(0,2)))+30*Integer.valueOf(dateAt.substring(3,5)) -
                    (Integer.valueOf(dbtask.getDateLimit(c).substring(0,2))+30*Integer.valueOf(dbtask.getDateLimit(c).substring(3,5)))
                    > 0) && dbtask.getDone(c).equals("0")) {
                //    title.setTextColor(Color.MAGENTA);
                view.setBackgroundColor(Color.parseColor("#c9735e"));
            }


            else if(((Integer.valueOf(dateAt.substring(0,2)))+30*Integer.valueOf(dateAt.substring(3,5)) -
                    (Integer.valueOf(dbtask.getDateLimit(c).substring(0,2))+30*Integer.valueOf(dbtask.getDateLimit(c).substring(3,5)))
                    >= -1) && dbtask.getDone(c).equals("0"))
                title.setTextColor(Color.RED);
            else if(((Integer.valueOf(dateAt.substring(0,2)))+30*Integer.valueOf(dateAt.substring(3,5)) -
                    (Integer.valueOf(dbtask.getDateLimit(c).substring(0,2))+30*Integer.valueOf(dbtask.getDateLimit(c).substring(3,5)))
                    >= -3)&& dbtask.getDone(c).equals("0"))
                title.setTextColor(Color.parseColor("#DAA520"));
            else
                title.setTextColor(Color.BLACK);


            //Se Task ainda não estiver com status "Done" o Checkbox fica habilitado
            if(dbtask.getDone(c).equals("1")) {
                checkDone.setChecked(true);
                checkDone.setEnabled(false);
                date.setText(dbtask.getDateDone(c));
            }
            else{
                checkDone.setChecked(false);
                checkDone.setEnabled(true);
                date.setText(dbtask.getDateLimit(c));
            }

        }
    }

    //Obtém-se o objeto do tipo Task que foi clicado e injetam-se seus dados nos elementos da tela, mudando para a aba do formulário
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CheckBox checkDone = (CheckBox) view.findViewById(R.id.checkDone);

        if(checkDone.isChecked() && checkDone.isEnabled()) { //Se o Checkbox estiver marcado então atualiza a Task pra "Done"
            if(abas.getCurrentTab() == 0) {
                cursorToDo.moveToPosition(position);
                dbtask.updateToDone(Integer.valueOf(dbtask.getId(cursorToDo)));

            }
            view.setBackgroundColor(Color.TRANSPARENT);
            ((TextView) view.findViewById(R.id.titulo)).setTextColor(Color.BLACK);

            //Recarrega as abas com uma pesquisa de Tasks atulizada
            cursorToDo.requery();
            cursorDone.requery();
            cursorTasks.requery();

        }
        else { //Se o Item foi selecionado para edição (não está com o checkbox ativo), então carregam os dados na aba ADD
            if (abas.getCurrentTab() == 0) {
                cursorToDo.moveToPosition(position);
                title.setText(dbtask.getTitle(cursorToDo));
                description.setText(dbtask.getDescription(cursorToDo));
                dateLimit.setText(dbtask.getDateLimit(cursorToDo));
                idTask.setText(dbtask.getId(cursorToDo));
                if (dbtask.getDone(cursorToDo).equals("1"))
                    checkDoneEdit.setChecked(true);
                else
                    checkDoneEdit.setChecked(false);

            } else if (abas.getCurrentTab() == 1) {
                cursorDone.moveToPosition(position);
                title.setText(dbtask.getTitle(cursorDone));
                description.setText(dbtask.getDescription(cursorDone));
                dateLimit.setText(dbtask.getDateLimit(cursorDone));
                idTask.setText(dbtask.getId(cursorDone));
                if (dbtask.getDone(cursorDone).equals("1"))
                    checkDoneEdit.setChecked(true);
                else
                    checkDoneEdit.setChecked(false);
            } else {
                cursorTasks.moveToPosition(position);
                title.setText(dbtask.getTitle(cursorTasks));
                description.setText(dbtask.getDescription(cursorTasks));
                dateLimit.setText(dbtask.getDateLimit(cursorTasks));
                idTask.setText(dbtask.getId(cursorTasks));
                if (dbtask.getDone(cursorTasks).equals("1"))
                    checkDoneEdit.setChecked(true);
                else
                    checkDoneEdit.setChecked(false);
            }
            view.setBackgroundColor(Color.TRANSPARENT);
            ((TextView) view.findViewById(R.id.titulo)).setTextColor(Color.BLACK);
            abas.setCurrentTab(3);

        }



    }


}
