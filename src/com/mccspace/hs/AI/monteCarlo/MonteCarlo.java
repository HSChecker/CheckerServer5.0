package com.mccspace.hs.AI.monteCarlo;

import com.mccspace.hs.AI.Continue;
import com.mccspace.hs.AI.Step;
import com.mccspace.hs.AI.monteCarlo.tree.Tree;
import com.mccspace.hs.service.game.CheckerBoard;
import com.mccspace.hs.service.game.gameplayer.GamePlayer;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.*;

/**
 * MonteCarlo��
 * Git to�� http://hs.mccspace.com:3000/Qing_ning/CheckerServer5.0/
 *
 * @TIME 2020/6/18 21:22
 * @AUTHOR ��˶~
 */

public class MonteCarlo implements GamePlayer {

    private static int timeS = 10;

    private CheckerBoard checkerBoard = CheckerBoard.newBoard();

    public MonteCarlo(){

    }

    @Override
    public List<Integer> waitPlay() {

        ExecutorService es = new ThreadPoolExecutor(6, 12, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for(int i=0;i<12;i++)
            es.submit(new runSeacherThread(checkerBoard));


        try {
            Thread.sleep(timeS*1000);
            Continue.ls1.stop();
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdownNow();


        Continue.ls1.clear();
        Step.ls1.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Tree.getTree().updateSQLTree();
            }
        }).start();
        return Tree.getTree().getNode(checkerBoard).getPro();
    }

    @Override
    public void updataChecker(CheckerBoard checkerBoard, List<Integer> upChess) {
        this.checkerBoard = checkerBoard;
    }

    @Override
    public JSONObject getInform() {
        return new JSONObject("{\"head\":\"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\",\"gamenum\":100,\"drawnum\":0,\"failnum\":0,\"winnum\":200,\"sex\":\"robat\",\"name\":\"���ؿ���\",\"user\":\"���ؿ���\",\"age\":1}");
    }
}