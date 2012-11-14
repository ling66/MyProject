//
//  PendingOperations.h
//  ClassicPhotos
//
//  Created by l l on 17/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PendingOperations : NSObject

@property (nonatomic,strong) NSMutableDictionary *downloadsInProgress;
@property (nonatomic,strong) NSOperationQueue *downloadQueue;

@property (nonatomic,strong) NSMutableDictionary *filtrationsInProgress;
@property (nonatomic,strong) NSOperationQueue *filtrationQueue;

@end
