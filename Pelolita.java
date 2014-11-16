import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;


@SuppressWarnings("serial")
public class Pelolita extends GLCanvas implements GLEventListener, KeyListener{

	private GL2 gl;
	private GLU glu;
	private GLUT glut;
	
	//Para definir as Coordenadas do sistema
	float xMin = -100f, xMax = 100f, yMin = -56.25f, yMax = 56.25f, zMin = -100f, zMax = 100f;
	

	// Define constants for the top-level container
	private static String TITULO = "Modelo 3D";
	private static final int CANVAS_LARGURA = 1024; // largura do drawable
	private static final int CANVAS_ALTURA = 768; // altura do drawable
	private static final int FPS = 60; // define frames per second para a animacao

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Cria a janela de renderizacao OpenGL
				GLCanvas canvas = new Pelolita();
				canvas.setPreferredSize(new Dimension(CANVAS_LARGURA, CANVAS_ALTURA));
				final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
				final JFrame frame = new JFrame(); 
				//frame.setUndecorated(true);
				//frame.setExtendedState(Frame.MAXIMIZED_BOTH);
				
				frame.getContentPane().add(canvas);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						new Thread() {
							@Override
							public void run() {
								if (animator.isStarted())
									animator.stop();
								System.exit(0);
							}
						}.start();
					}
				});
				frame.setTitle(TITULO);
				frame.pack();
				frame.setLocationRelativeTo(null); //Center frame
				frame.setVisible(true);
				animator.start(); // inicia o loop de animacao
			}
		});
	}


	/** Construtor da classe */
	public Pelolita() {
		this.addGLEventListener(this);

		this.addKeyListener(this);
		this.setFocusable(true);
		this.requestFocus();
	}

	/**
	 *Chamado uma vez quando o contexto OpenGL eh criado
	 */
	@Override
	public void init(GLAutoDrawable drawable) {		
		gl = drawable.getGL().getGL2(); // obtem o contexto grafico OpenGL	
		glu = new GLU(); 
		

		
		//((Component) drawable).addKeyListener(this);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}

	/**
	 * Chamado quando a janela eh redimensionada
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {		
		gl = drawable.getGL().getGL2(); // obtem o contexto grafico OpenGL

		// Ativa a matriz de projecao
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		// Projecao ortogonal 3D			
		gl.glOrtho(xMin, xMax, yMin, yMax, zMin, zMax);


		// Ativa a matriz de modelagem
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		System.out.println("Reshape: " + width + " " + height);
	}

	/**
	 * Chamado para renderizar a imagem do GLCanvas pelo animator
	 */
	@Override
	public void display(GLAutoDrawable drawable) {		
		gl = drawable.getGL().getGL2(); // obtem o contexto grafico OpenGL		
		glut = new GLUT();
		
		// Especifica que a cor para limpar a janela de visualizacao eh preta
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Limpa a janela de visualizacao com a cor de fundo especificada
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		
		//Redefine a matriz atual com a matriz "identidade"
		gl.glLoadIdentity();

		// criar a cena aqui....	
		
		switch(states){ 
		
		case 0: //tela menu
			showMenu();
			boxSelector();
			break;
		
		case 1: //tela jogo fase 1
			drawBall();
			base();
			movimentBall();
			break;
		
		case 2: //tela jogo fase 2

			break;

		}
		
		// Executa os comandos OpenGL
		gl.glFlush();
	}

	float ballX = 0, ballY = 0;
	float xSpeed = 1.2f, ySpeed = 1.2f;
	float desvio = 0.1f;
	float sizeBall = 4;
	
	float ballXMin = xMin+sizeBall, ballXMax = xMax-sizeBall;
	float ballYMin = yMin+sizeBall, ballYMax = yMax-sizeBall;
	float baseX = 0;
	
	int states = 0;
	
	public void base() {
		//
		gl.glPushMatrix();
		gl.glTranslatef(baseX, -54.25f, 0);
		//
		gl.glColor3f(1.0f, 0.5f, 0.0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(-15,2);
		gl.glVertex2f(-15,-2);
		gl.glVertex2f(15,-2);
		gl.glVertex2f(15,2);
		
		gl.glEnd();
		//
		gl.glPopMatrix();
	}
	
	public void movimentBall(){
		   // 
		   ballX += xSpeed;
		   ballY += ySpeed;
		   // 
		   if (ballX > ballXMax) {
		      ballX = ballXMax;
		      xSpeed = -xSpeed+desvio;
		   } else if (ballX < ballXMin) {
		      ballX = ballXMin;
		      xSpeed = -xSpeed;
		   }
		   if (ballY > ballYMax) {
		      ballY = ballYMax+desvio;
		      ySpeed = -ySpeed;
		   } else if ( ballY < (yMin+8) && (ballX<=baseX+15 && ballX>=baseX-15)   ) {
			  System.out.println(ballX);
			  System.out.println(baseX);
		      //ballY = ballYMin;
			  ballY = yMin+sizeBall*2;
		      ySpeed = -ySpeed;

		   }else if (ballY < ballYMin) {

			      //ballY = ballYMin;
			      //ySpeed = -ySpeed;
			   ballY = 0;
			   ballX = 0;
			   ySpeed = 1.2f;
				   mensagem();
		   }
		   
		   
	}
	
	
	
	public void showMenu(){
		
		float _x = -50f;
		float _y = -10f;
		
		gl.glPushMatrix();
		gl.glColor3f(1,1,1);
		
		gl.glRasterPos2f(_x, _y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "INICAR JOGO");
		
		gl.glRasterPos2f(_x, _y-10f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "REGRAS DO JOGO");
		
		gl.glRasterPos2f(_x, _y-20f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "CREDITOS");
		
		gl.glPopMatrix();
	}
	
	float xBoxSelector = -50f;
	float yBoxSelector = 1f;
	
	public void boxSelector(){
		//
		float _width = 10f;
		float _heigth = 5f;
		
		gl.glPushMatrix();
		gl.glTranslatef(xBoxSelector, yBoxSelector, 0);
		//
		gl.glLineWidth(2);
		gl.glColor3f(1.0f,0.0f,1.0f);
		gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex2f( -1*(_width/2) ,_heigth/2);
			gl.glVertex2f( -1*(_width/2) ,-1*(_heigth/2));
			//gl.glVertex2f( (_width/2) ,-1*(_heigth/2));
			//gl.glVertex2f( (_width/2) ,_heigth/2 );        
		gl.glEnd();
		//
		gl.glPopMatrix();
	}
	
	public void drawBall(){
			gl.glPushMatrix();
			//
			gl.glColor3f(1.0f, 0.5f, 0.0f);
			gl.glTranslatef(ballX, ballY, 0);
			glut.glutSolidSphere(sizeBall, 15, 20);
			//
			gl.glPopMatrix();
	}
	
	public void mensagem(){
		
		gl.glColor3f(1,1,1);
		gl.glRasterPos2f(0f, 0f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, "PERDEU!");
	}
	
	/**
	 * Chamado quando o contexto OpenGL eh destruido
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch(keyCode){
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;

			case KeyEvent.VK_LEFT:
				if (baseX > -85) {
					baseX += -17;
				}
				break;

			case KeyEvent.VK_RIGHT:

				if (baseX < 85) {
					baseX += 17;
				}

				break;
				
			case KeyEvent.VK_UP:
				yBoxSelector += 10;
				break;
			
			case KeyEvent.VK_DOWN:
				yBoxSelector -= 10;
				break;
		}
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
