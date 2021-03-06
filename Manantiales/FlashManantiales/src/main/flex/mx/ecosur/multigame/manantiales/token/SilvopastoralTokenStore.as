package mx.ecosur.multigame.manantiales.token
{
	
    import flash.events.MouseEvent;

import mx.ecosur.multigame.manantiales.token.ManantialesToken;

import mx.events.DragEvent;
    import mx.ecosur.multigame.manantiales.enum.TokenType;
	
	public class SilvopastoralTokenStore extends ManantialesTokenStore
	{
		public function SilvopastoralTokenStore()
		{
			super();
			_tokenType = TokenType.SILVOPASTORAL;
		}
		
        public override function addToken():void{
            var token:SilvopastoralToken = new SilvopastoralToken();
            token.buttonMode = false;
            token.addEventListener(MouseEvent.MOUSE_OVER, selectToken);
            token.addEventListener(MouseEvent.MOUSE_OUT, unselectToken);
            if (_startMoveHandler != null){
                    token.addEventListener(MouseEvent.MOUSE_DOWN, _startMoveHandler);
                }
            if (_endMoveHandler != null){
                token.addEventListener(DragEvent.DRAG_COMPLETE, _endMoveHandler);           
            }
            addChild(token);
            _nTokens ++;
        }
    }
}
